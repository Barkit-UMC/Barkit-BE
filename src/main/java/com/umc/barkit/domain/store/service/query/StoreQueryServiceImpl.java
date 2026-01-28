package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.store.dto.google.GooglePlaceDTO;
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
import com.umc.barkit.domain.store.external.kakao.KakaoMapSearchClient;
import com.umc.barkit.domain.store.external.kakao.dto.KakaoResDTO;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandRepository;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreQueryServiceImpl implements StoreQueryService{

    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreRepository storeRepository;
    private final StoreBrandRepository storeBrandRepository;
    private final KakaoMapSearchClient kakaoClient;
    private final GoogleMapSearchClient googleClient;
    private final WebClient kakaoWebClient;

    @Value("${google.api.key}")
    private String googleApiKey;

    private final WebClient googleWebClient;


    @Override
    public List<StoreResDTO.SearchedStore> search(
            String query,
            DistanceType distanceType,
            Category category,
            Double userLat,
            Double userLng,
            Double centerLat,
            Double centerLng,
            Sort sort
    ) {

        //1. 입력값이 멤버십명인지 매장명인지 구별
        if (isMembershipBrand(query)) {
            // 멤버십명으로 검색 로직 실행
            return searchByMembership(
                    query,
                    distanceType,
                    category,
                    userLat,
                    userLng,
                    centerLat,
                    centerLng,
                    sort);
        } else if (isStoreBrand(query)) {
            // 매장명으로 검색 로직 실행
            return List.of();
        } else {
            //검색 결과 없음 오류 발생
            return List.of();
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
            Double centerLng,
            Sort sort) {

        Optional<MembershipBrand> membership = membershipBrandRepository.findByName(query);
        List<Long> storeBrandIds = storeBrandMembershipBrandRepository.findStoreBrandIdsByMembershipBrandId(membership.get().getId());

        List<String> storeNames = new ArrayList<>();
        List<Long> filteredStoreBrandIds = new ArrayList<>();
        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        //카테고리 구분
        categoryChecking(category, storeBrandIds, storeNames, filteredStoreBrandIds);

        //외부 API 호출 및 Store 저장
        for (int i = 0; i < storeNames.size(); i++) {
            String storeName = storeNames.get(i);
            Long storeBrandId = filteredStoreBrandIds.get(i);

            Double sendLat = (distanceType == DistanceType.CURRENT) ? userLat : centerLat;
            Double sendLng = (distanceType == DistanceType.CURRENT) ? userLng : centerLng;

            // 카카오: 근처 매장 리스트
            List<KakaoResDTO.Document> documents =
                    kakaoClient.searchByKeyword(storeName, sendLat, sendLng);

            // store 저장 + StoreResDTO 매핑해서 결과에 합치기
            result.addAll(saveStoreAndReturnResDTO(storeBrandId, documents, userLat, userLng, sendLat, sendLng));
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
                }
                //검색 결과 없을 경우 추가해야함
            }
        } else if (category == Category.ALL) {
            for (Long storeBrandId : storeBrandIds) {
                storeNames.add(storeBrandRepository.findNameById(storeBrandId));
                filteredStoreBrandIds.add(storeBrandId);
            }
        }
    }

    private static final double MAX_DISTANCE_KM = 5.0;

    private List<StoreResDTO.SearchedStore> saveStoreAndReturnResDTO(
            Long storeBrandId,
            List<KakaoResDTO.Document> documents,
            Double userLat,
            Double userLng,
            Double sendLat,
            Double sendLng
    ) {
        if (documents == null || documents.isEmpty()) return List.of();

        StoreBrand storeBrand = storeBrandRepository.findById(storeBrandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 store_brand"));

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO = findMembershipsForStoreBrand(storeBrandId);

        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        for (KakaoResDTO.Document doc : documents) {

            double storeLat = Double.parseDouble(doc.y());
            double storeLng = Double.parseDouble(doc.x());

            double distKm = distanceKm(sendLat, sendLng, storeLat, storeLng);

            if (distKm > MAX_DISTANCE_KM) continue;

            GoogleResDTO.Place googlePlace = getGooglePlace(doc);
            String googleId = (googlePlace != null) ? googlePlace.id() : null;

            if (googleId == null) continue;

            Store store = storeRepository.findByGoogleId(googleId).orElse(null);


            if (store == null) {
                store = storeRepository.save(
                        Store.builder()
                                .googleId(googleId)
                                .brand(storeBrand)
                                .build()
                );
            }

            //응답 DTO 매핑
            result.add(mapToSearchedStore(doc, googlePlace, membershipsDTO, userLat, userLng));
        }

        return result;
    }


    private GoogleResDTO.Place getGooglePlace(KakaoResDTO.Document doc) {
        String textQuery = buildGoogleTextQuery(doc); // "매장명 + 주소"
        Double lat = Double.parseDouble(doc.y());
        Double lng = Double.parseDouble(doc.x());

        List<GoogleResDTO.Place> places = googleClient.searchText(textQuery, lat, lng);

        if (places == null || places.isEmpty()) return null;
        return places.get(0);
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

    private String buildGoogleTextQuery(KakaoResDTO.Document doc) {
        String address = (doc.road_address_name() != null && !doc.road_address_name().isBlank())
                ? doc.road_address_name()
                : doc.address_name();
        return doc.place_name() + " " + address;
    }

    //응답 DTO 매핑
    private StoreResDTO.SearchedStore mapToSearchedStore(
            KakaoResDTO.Document doc,
            GoogleResDTO.Place googlePlace,
            List<StoreResDTO.SearchedStoreMembership> memberships,
            Double userLat,
            Double userLng
    ) {
        Double storeLat = Double.parseDouble(doc.y());
        Double storeLng = Double.parseDouble(doc.x());

        String address = (doc.road_address_name() != null && !doc.road_address_name().isBlank())
                ? doc.road_address_name()
                : doc.address_name();

        Double distanceKm = round2(distanceKm(userLat, userLng, storeLat, storeLng));

        String directionUrl = buildKakaoDirectionUrl(doc.place_name(), storeLat, storeLng);

        String photoUrl = (googlePlace == null) ? null : googleClient.getThumbnailPhotoUrl(googlePlace);


        return StoreResDTO.SearchedStore.builder()
                .name(doc.place_name())
                .location(StoreResDTO.SearchedStoreLocation.builder()
                        .lat(storeLat)
                        .lng(storeLng)
                        .build())
                .address(address)
                .phone(doc.phone())
                .memberships(memberships)
                .distanceKm(distanceKm)
                .directionUrl(directionUrl)
                .photoUrl(photoUrl)
                .build();
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

    //카카오 길찾기 url
    private String buildKakaoDirectionUrl(String name, Double lat, Double lng) {
        return "https://map.kakao.com/link/to/"
                + java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8)
                + "," + lat + "," + lng;
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

        // 8) 최종 DTO 조립
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
