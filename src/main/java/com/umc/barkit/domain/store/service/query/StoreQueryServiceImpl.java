package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.store.dto.google.GooglePlaceDTO;
import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.entity.mapping.StoreBrandMembershipBrand;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;
import com.umc.barkit.domain.store.exception.StoreException;
import com.umc.barkit.domain.store.exception.code.StoreErrorCode;
import com.umc.barkit.domain.store.external.google.GoogleMapSearchClient;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandRepository;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.domain.store.repository.projection.ViewCountProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreQueryServiceImpl implements StoreQueryService{

    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreRepository storeRepository;
    private final StoreBrandRepository storeBrandRepository;
    private final GoogleMapSearchClient googleClient;


    @Value("${google.api.key}")
    private String googleApiKey;

    private final WebClient googleWebClient;

    private static final double MAX_DISTANCE_KM = 5.0;

    @Override
    public StoreResDTO.SearchedStoreSlice search(
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
            Sort sort,
            int cursor,
            int size
    ) {

        List<StoreResDTO.SearchedStore> fullResult;

        // 입력값이 멤버십명인지 매장명인지 구별
        if (isMembershipBrand(req.query())) {
            // 멤버십명으로 검색 로직 실행
            fullResult = searchByMembership(
                    req.query(),
                    distanceType,
                    category,
                    req.userLat(),
                    req.userLng(),
                    req.centerLat(),
                    req.centerLng()
            );
        } else if (isStoreBrand(req.query())) {
            // 매장명으로 검색 로직 실행
            fullResult = List.of();
        } else {
            //검색 결과 없음 오류 발생 or 빈리스트 반환
            fullResult = List.of();
        }

        //중복 제거
        List<StoreResDTO.SearchedStore> deduped = deduplicateByGoogleId(fullResult);

        //정렬
        sortInPlace(deduped, sort);

        //페이징
        int from = Math.min(cursor, deduped.size());
        int to = Math.min(cursor + size, deduped.size());

        List<StoreResDTO.SearchedStore> page = deduped.subList(from, to);
        boolean hasNext = to < deduped.size();
        int nextCursor = to;

        return new StoreResDTO.SearchedStoreSlice(page, hasNext, nextCursor);

    }

    private List<StoreResDTO.SearchedStore> deduplicateByGoogleId(List<StoreResDTO.SearchedStore> list) {
        if (list == null || list.isEmpty()) return List.of();
        Map<String, StoreResDTO.SearchedStore> map = new LinkedHashMap<>();
        for (StoreResDTO.SearchedStore s : list) {
            String key = s.googleId();
            if (key == null) {
                key = UUID.randomUUID().toString();
            }
            map.putIfAbsent(key, s);
        }
        return new ArrayList<>(map.values());
    }

    private void sortInPlace(List<StoreResDTO.SearchedStore> searchedStores, Sort sort) {
        if (searchedStores == null || searchedStores.size() <= 1) return;

        if (sort == Sort.DISTANCE) {
            searchedStores.sort(Comparator.comparing(StoreResDTO.SearchedStore::distanceKm,
                    Comparator.nullsLast(Double::compareTo)));
            return;
        }

        if (sort == Sort.POPULAR) {

            List<String> googleIds = searchedStores.stream()
                    .map(StoreResDTO.SearchedStore::googleId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            if (googleIds.isEmpty()) return;

            Map<String, Long> viewCountMap =
                    storeRepository.findViewCountsByGoogleIds(googleIds).stream()
                            .collect(Collectors.toMap(
                                    ViewCountProjection::getGoogleId,
                                    p -> Optional.ofNullable(p.getViewCount()).orElse(0L)
                            ));

            searchedStores.sort((a, b) -> {
                long va = viewCountMap.getOrDefault(a.googleId(), 0L);
                long vb = viewCountMap.getOrDefault(b.googleId(), 0L);

                int cmp = Long.compare(vb, va);
                if (cmp != 0) return cmp;

                //viewCount 같으면 거리순
                return Comparator.comparing(
                        StoreResDTO.SearchedStore::distanceKm,
                        Comparator.nullsLast(Double::compareTo)
                ).compare(a, b);
            });
        }
    }

    //멤버십명인지 검증
    private Boolean isMembershipBrand(String query) {
        return membershipBrandRepository.existsByName(query);
    }

    //매장명인지 검증
    private Boolean isStoreBrand(String query) {
        return null;
    }

    //매장명 검색 로직(seoyeon)

    //멤버십명 검색 로직(remy)
    private List<StoreResDTO.SearchedStore> searchByMembership(
            String query,
            DistanceType distanceType,
            Category category,
            Double userLat,
            Double userLng,
            Double centerLat,
            Double centerLng) {

        Optional<MembershipBrand> membership = membershipBrandRepository.findByName(query);
        if (membership.isEmpty()) return List.of();

        List<Long> storeBrandIds =
                storeBrandMembershipBrandRepository.findStoreBrandIdsByMembershipBrandId(membership.get().getId());

        List<String> storeNames = new ArrayList<>();
        List<Long> filteredStoreBrandIds = new ArrayList<>();
        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        //카테고리 구분
        categoryChecking(category, storeBrandIds, storeNames, filteredStoreBrandIds);

        /*카테고리 구분 후 일치하는 겁색결과가 없어서
        storeNames, filteredStoreBrandIds 빈 리스트 일 때*/
        if (storeNames.isEmpty() && filteredStoreBrandIds.isEmpty()) {
            return result;
        }

        //외부 API 호출 및 Store 저장
        for (int i = 0; i < storeNames.size(); i++) {
            String storeName = storeNames.get(i);
            Long storeBrandId = filteredStoreBrandIds.get(i);

            Double sendLat = (distanceType == DistanceType.CURRENT) ? userLat : centerLat;
            Double sendLng = (distanceType == DistanceType.CURRENT) ? userLng : centerLng;

            //매장명으로 구글 API 호출
            List<GoogleResDTO.Place> places = googleClient.searchText(storeName, sendLat, sendLng);

            // store 저장 + StoreResDTO 매핑해서 결과에 합치기
            result.addAll(saveStoreAndReturnResDTO(storeBrandId, places, userLat, userLng, sendLat, sendLng));
        }

        return result;
    }

    //카테고리 구분 메서드
    private void categoryChecking(Category category, List<Long> storeBrandIds, List<String> storeNames, List<Long> filteredStoreBrandIds) {
        if (category != Category.ALL) {
            for (Long storeBrandId : storeBrandIds) {
                Optional<String> storeName = storeBrandRepository.findNameByIdAndCategory(storeBrandId, category);
                if (storeName.isPresent()) {
                    storeNames.add(storeName.get());
                    filteredStoreBrandIds.add(storeBrandId);
                } else {
                    return; // 빈 리스트
                }
            }
        } else if (category == Category.ALL) {
            for (Long storeBrandId : storeBrandIds) {
                storeNames.add(storeBrandRepository.findNameById(storeBrandId));
                filteredStoreBrandIds.add(storeBrandId);
            }
        }
    }

    private List<StoreResDTO.SearchedStore> saveStoreAndReturnResDTO(
            Long storeBrandId,
            List<GoogleResDTO.Place> places,
            Double userLat,
            Double userLng,
            Double sendLat,
            Double sendLng
    ) {
        if (places == null || places.isEmpty()) return List.of();

        StoreBrand storeBrand = storeBrandRepository.findById(storeBrandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 store_brand"));

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO = findMembershipsForStoreBrand(storeBrandId);

        Map<String, GoogleResDTO.Place> uniquePlacesById = new LinkedHashMap<>();

        for (GoogleResDTO.Place p : places) {
            if (p == null || p.id() == null) continue;

            if (p.location() == null) continue;

            uniquePlacesById.putIfAbsent(p.id(), p);
        }

        if (uniquePlacesById.isEmpty()) return List.of();

        List<String> googleIds = new ArrayList<>(uniquePlacesById.keySet());

        Map<String, Store> existingByGoogleId =
                storeRepository.findAllByGoogleIdIn(googleIds).stream()
                        .collect(Collectors.toMap(Store::getGoogleId, Function.identity()));

        // DB에 없는 googleId만 모아서 한 번에 저장
        List<Store> toSave = new ArrayList<>();
        for (String gid : googleIds) {
            if (!existingByGoogleId.containsKey(gid)) {
                toSave.add(Store.builder()
                        .googleId(gid)
                        .brand(storeBrand)
                        .build());
            }
        }

        if (!toSave.isEmpty()) {
            List<Store> saved = storeRepository.saveAll(toSave);
            for (Store s : saved) {
                existingByGoogleId.put(s.getGoogleId(), s);
            }
        }


        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        for (String gid : googleIds) {
            GoogleResDTO.Place p = uniquePlacesById.get(gid);
            Store store = existingByGoogleId.get(gid);
            if (p == null || store == null) continue;

            double storeLat = p.location().latitude();
            double storeLng = p.location().longitude();

            double distKmFromSendPoint = distanceKm(sendLat, sendLng, storeLat, storeLng);
            if (distKmFromSendPoint > MAX_DISTANCE_KM) continue;

            result.add(mapToSearchedStore(p, store.getId(), membershipsDTO, userLat, userLng));

        }

        return result;
    }

    private List<StoreResDTO.SearchedStoreMembership> findMembershipsForStoreBrand(Long storeBrandId) {
        List<Long> membershipIds =
                storeBrandMembershipBrandRepository.findMembershipBrandIdsByStoreBrandId(storeBrandId);

        if (membershipIds == null || membershipIds.isEmpty()) {
            return List.of();
        }

        List<MembershipBrand> membershipBrands =
                membershipBrandRepository.findAllById(membershipIds);

        return membershipBrands.stream()
                .map(m -> StoreResDTO.SearchedStoreMembership.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .logoUrl(m.getLogoUrl()) // MembershipBrand에 logoUrl 필드가 있다고 가정
                        .build())
                .toList();
    }


    //응답 DTO 매핑
    private StoreResDTO.SearchedStore mapToSearchedStore(
            GoogleResDTO.Place p,
            Long storeId,
            List<StoreResDTO.SearchedStoreMembership> memberships,
            Double userLat,
            Double userLng
    ) {
        double storeLat = p.location().latitude();
        double storeLng = p.location().longitude();

        Double distanceKm = round2(distanceKm(userLat, userLng, storeLat, storeLng));

        //일단 구글 길찾기로
        String directionUrl = buildGoogleDirectionUrl(p.id(), storeLat, storeLng);


        return StoreResDTO.SearchedStore.builder()
                .storeId(storeId)
                .googleId(p.id())
                .name(p.displayName())
                .location(StoreResDTO.SearchedStoreLocation.builder()
                        .lat(storeLat)
                        .lng(storeLng)
                        .build())
                .address(p.formattedAddress())
                .phone(p.nationalPhoneNumber())
                .memberships(memberships)
                .distanceKm(distanceKm)
                .directionUrl(directionUrl)
                .build();
    }

    //구글 길찾기 url
    private String buildGoogleDirectionUrl(String placeId, double lat, double lng) {
        return "https://www.google.com/maps/dir/?api=1"
                + "&destination=" + lat + "," + lng
                + "&destination_place_id=" + java.net.URLEncoder.encode(placeId, java.nio.charset.StandardCharsets.UTF_8);
    }

    private double round2(double v) {
        return Math.round(v * 100) / 100.0;
    }

    private static final double EARTH_RADIUS_KM = 6371.0;

    //위도, 경도로 사용자 현재위치와 매장 사이의 거리(km) 계산
    private double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }


    @Override
    public StoreResDTO.StoreDetail detail(String placeId, Double userLat, Double userLng) {

        // DB 조회
        Store store = storeRepository.findByGoogleId(placeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE4001));

        // Google Places API 호출
        GooglePlaceDTO.Place g =
                googleWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v1/places/{placeId}")
                                .build(placeId))
                        .header("X-Goog-Api-Key", googleApiKey)
                        .header("X-Goog-FieldMask",
                                "id,displayName,formattedAddress,websiteUri,location,photos,currentOpeningHours.openNow,currentOpeningHours.weekdayDescriptions,nationalPhoneNumber")
                        .retrieve()
                        .bodyToMono(GooglePlaceDTO.Place.class)
                        .block();


        // 위치 계산
        double storeLat = g.location().latitude();
        double storeLng = g.location().longitude();

        StoreResDTO.StoreLocation location = StoreResDTO.StoreLocation.builder()
                .lat(storeLat)
                .lng(storeLng)
                .build();

        double distance = distanceKm(userLat, userLng, storeLat, storeLng);

        // 4) 영업시간 정보
        boolean isOpen = g.currentOpeningHours() != null && g.currentOpeningHours().openNow();
        List<String> weekdayText =
                g.currentOpeningHours() != null ? g.currentOpeningHours().weekdayDescriptions() : null;

        StoreResDTO.StoreHourInfo hourInfo = StoreResDTO.StoreHourInfo.builder()
                .weekdayText(weekdayText)
                .isOpen(isOpen)
                .build();


        // 사진 URL 변환
        List<StoreResDTO.StorePhotoInfo> photos =
                (g.photos() == null) ? List.of() :
                        g.photos().stream()
                                .map(p -> StoreResDTO.StorePhotoInfo.builder()
                                        .url("https://places.googleapis.com/v1/" + p.name()
                                                + "/media?key=" + googleApiKey)
                                        .width(p.widthPx())
                                        .height(p.heightPx())
                                        .build())
                                .toList();


        // 연락 정보
        StoreResDTO.StoreContact contact = StoreResDTO.StoreContact.builder()
                .address(g.formattedAddress())
                .phoneNumber(g.nationalPhoneNumber())
                .homepage(g.websiteUri())
                .build();


        // 멤버십 조회
        List<StoreBrandMembershipBrand> membershipEntities =
                storeBrandMembershipBrandRepository.findByStoreBrandId(store.getBrand().getId());

        List<StoreResDTO.MembershipInfo> membershipInfos = membershipEntities.stream()
                .map(m -> StoreResDTO.MembershipInfo.builder()
                        .name(m.getMembershipBrand().getName())
                        .logoUrl(m.getMembershipBrand().getLogoUrl())
                        .build())
                .toList();

        // DTO 조립
        return StoreResDTO.StoreDetail.builder()
                .name(g.displayName() != null ? g.displayName().text() : "이름 정보 없음")
                .distance(distance)
                .location(location)
                .contact(contact)
                .hourInfo(hourInfo)
                .membership(membershipInfos)
                .photos(photos)
                .build();
    }



}
