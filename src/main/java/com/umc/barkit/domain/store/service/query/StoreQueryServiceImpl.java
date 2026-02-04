package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandAliasRepository;
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
import com.umc.barkit.domain.store.repository.StoreBrandAliasRepository;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandRepository;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.domain.store.repository.projection.BrandIdNameProjection;
import com.umc.barkit.domain.store.repository.projection.BrandMembershipProjection;
import com.umc.barkit.domain.store.repository.projection.ViewCountProjection;
import com.umc.barkit.domain.store.service.command.StoreCommandService;
import com.umc.barkit.domain.store.util.StoreUtil;
import lombok.RequiredArgsConstructor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreQueryServiceImpl implements StoreQueryService{

    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreRepository storeRepository;
    private final StoreBrandRepository storeBrandRepository;
    private final StoreBrandAliasRepository storeBrandAliasRepository;
    private final MembershipBrandAliasRepository membershipBrandAliasRepository;

    private final GoogleMapSearchClient googleClient;
    private final StoreCommandService storeCommandService;

    @Qualifier("googleSearchExecutor")
    private final Executor googleSearchExecutor;

    //목록 조회(멤버십/매장)
    @Override
    public StoreResDTO.SearchedStoreSlice search(
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
            Sort sort,
            int cursor,
            int size
    ) {

        long t0 = System.nanoTime();

        List<StoreResDTO.SearchedStore> fullResult;

        // 우선 검색어가 멤버십이라 가정하고 멤버십명으로 쿼리 정규화
        Optional<MembershipBrand> membershipOpt = resolveMembership(req.query());

        // 멤버십명으로 검색
        if (membershipOpt.isPresent()) {
            fullResult = searchByMembership(
                    membershipOpt.get(),
                    distanceType,
                    category,
                    req.userLat(),
                    req.userLng(),
                    req.centerLat(),
                    req.centerLng()
            );
        }
        // 매장명으로 검색
        else {
            Optional<StoreBrand> brandOpt = resolveStoreBrand(req.query());
            if (brandOpt.isPresent()) {
                fullResult = searchByBrand(
                        brandOpt.get(),
                        req.query(),
                        distanceType,
                        category,
                        req.userLat(),
                        req.userLng(),
                        req.centerLat(),
                        req.centerLng()
                );
            }
            // 멤버십도 매장도 아니면 빈리스트 반환
            else {
                fullResult = List.of();
            }
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


        long elapsedMs = (System.nanoTime() - t0) / 1_000_000;
        log.info("소요 시간 : " + elapsedMs);

        return new StoreResDTO.SearchedStoreSlice(page, hasNext, nextCursor);

    }

    @Transactional
    @Override
    public StoreResDTO.StoreDetail detail(String googleId, Double userLat, Double userLng) {

        // DB 조회
        Store store = storeRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE4001));

        // 조회수
        store.addViewCount();

        // Google Places API 호출
        GooglePlaceDTO.Place g = googleClient.getPlaceDetail(googleId);


        // 위치 계산
        double storeLat = g.location().latitude();
        double storeLng = g.location().longitude();

        StoreResDTO.StoreLocation location = StoreResDTO.StoreLocation.builder()
                .lat(storeLat)
                .lng(storeLng)
                .build();

        double distance = StoreUtil.round2(StoreUtil.distanceKm(userLat, userLng, storeLat, storeLng));

        // 영업시간 정보
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
                                        .url(googleClient.buildPhotoMediaUrl(p.name(), 400))
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

    //매장명 검색 로직
    /*
    올리브영만 검색 -> 근처 검색
    올리브영 성수점 검색 -> 근처 검색 실행 -> 결과 없음 -> 전체 검색
     */
    private List<StoreResDTO.SearchedStore> searchByBrand(
            StoreBrand matchedBrand,
            String originalQuery,
            DistanceType distanceType,
            Category category,
            Double userLat, Double userLng,
            Double centerLat, Double centerLng) {


        // 카테고리 체크
        if (category != Category.ALL && matchedBrand.getCategory() != category) {
            return List.of();
        }

        // 근처 검색
        List<StoreResDTO.SearchedStore> near = searchBrandNearby(
                originalQuery, matchedBrand, distanceType, userLat, userLng, centerLat, centerLng
        );

        if (near != null && !near.isEmpty()) {
            return near;
        }

        // 근처 결과가 없으면 전체 검색
        return searchBrandGlobal(
                originalQuery, matchedBrand, userLat, userLng
        );
    }

    // 근처 매장 검색
    private List<StoreResDTO.SearchedStore> searchBrandNearby(
            String query,
            StoreBrand matchedBrand,
            DistanceType distanceType,
            Double userLat,
            Double userLng,
            Double centerLat,
            Double centerLng) {

        Double sendLat = (distanceType == DistanceType.CURRENT) ? userLat : centerLat;
        Double sendLng = (distanceType == DistanceType.CURRENT) ? userLng : centerLng;

        List<GoogleResDTO.Place> places = googleClient.searchText(query, sendLat, sendLng);
        if (places == null || places.isEmpty()) return List.of();

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                findMembershipsForStoreBrand(matchedBrand.getId());

        // 검색 반경 5km 필터 적용
        return storeCommandService.saveAndMap(
                matchedBrand,
                places,
                userLat,
                userLng,
                sendLat,
                sendLng,
                true,
                membershipsDTO
        );

    }

    // 전체 검색
    private List<StoreResDTO.SearchedStore> searchBrandGlobal(
            String query,
            StoreBrand matchedBrand,
            Double userLat,
            Double userLng
    ) {
        // 위치 없이 호출
        List<GoogleResDTO.Place> places = googleClient.searchText(query);
        if (places == null || places.isEmpty()) return List.of();

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                findMembershipsForStoreBrand(matchedBrand.getId());

        // 검색 반경 5km 필터 미적용
        return storeCommandService.saveAndMap(
                matchedBrand,
                places,
                userLat,
                userLng,
                userLat,
                userLng,
                false,
                membershipsDTO
        );
    }

    //멤버십명 검색 로직
    private List<StoreResDTO.SearchedStore> searchByMembership(
            MembershipBrand membership,
            DistanceType distanceType,
            Category category,
            Double userLat,
            Double userLng,
            Double centerLat,
            Double centerLng) {


        List<Long> storeBrandIds =
                storeBrandMembershipBrandRepository.findStoreBrandIdsByMembershipBrandId(membership.getId());

        if (storeBrandIds == null || storeBrandIds.isEmpty()) return List.of();

        // 카테고리 필터링 (IN 절 사용해서 한번에 조회)
        List<BrandIdNameProjection> idNames =
                storeBrandRepository.findIdNameByIdsAndCategory(storeBrandIds, category);

        // 매칭되는 카테고리 없으면 빈 리스트 반환
        if (idNames == null || idNames.isEmpty()) return List.of();

        List<Long> filteredBrandIds = idNames.stream().map(BrandIdNameProjection::getId).toList();
        List<String> storeNames = idNames.stream().map(BrandIdNameProjection::getName).toList();

        // StoreBrand도 한번에 가져오기
        Map<Long, StoreBrand> storeBrandMap =
                storeBrandRepository.findAllById(filteredBrandIds).stream()
                        .collect(Collectors.toMap(StoreBrand::getId, sb -> sb));

        // 멤버십 DTO도 한 번에 가져오기
        Map<Long, List<StoreResDTO.SearchedStoreMembership>> membershipsByBrandId =
                preloadMembershipsDTO(filteredBrandIds);

        // 구글 api 호출 시 기준이 될 위도, 경도 정하기
        Double sendLat = (distanceType == DistanceType.CURRENT) ? userLat : centerLat;
        Double sendLng = (distanceType == DistanceType.CURRENT) ? userLng : centerLng;

        // 구글 api 호출 병렬 처리
        List<CompletableFuture<GooglePlaceDTO.BrandPlacesResult>> futures = new ArrayList<>();

        for (int i = 0; i < storeNames.size(); i++) {
            String storeName = storeNames.get(i);
            Long storeBrandId = filteredBrandIds.get(i);

            futures.add(CompletableFuture.supplyAsync(() -> {
                        List<GoogleResDTO.Place> places = googleClient.searchText(storeName, sendLat, sendLng);
                        return new GooglePlaceDTO.BrandPlacesResult(storeBrandId, storeName, places);
                    }, googleSearchExecutor)
                    .orTimeout(3, TimeUnit.SECONDS)
                    .exceptionally(ex -> new GooglePlaceDTO.BrandPlacesResult(storeBrandId, storeName, List.of())));
        }

        List<GooglePlaceDTO.BrandPlacesResult> brandResults = futures.stream()
                .map(CompletableFuture::join)
                .toList();


        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        // api 호출 결과로 가져온 매장 db에 없으면 저장
        for (GooglePlaceDTO.BrandPlacesResult r : brandResults) {
            if (r.places() == null || r.places().isEmpty()) continue;

            StoreBrand sb = storeBrandMap.get(r.storeBrandId());
            if (sb == null) continue;

            List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                    membershipsByBrandId.getOrDefault(r.storeBrandId(), List.of());

            result.addAll(storeCommandService.saveAndMap(
                    sb,
                    r.places(),
                    userLat,
                    userLng,
                    sendLat,
                    sendLng,
                    true,
                    membershipsDTO
            ));
        }

        return result;
    }


    private Map<Long, List<StoreResDTO.SearchedStoreMembership>> preloadMembershipsDTO(List<Long> storeBrandIds) {
        List<BrandMembershipProjection> rows =
                storeBrandMembershipBrandRepository.findMembershipsByStoreBrandIds(storeBrandIds);

        if (rows == null || rows.isEmpty()) return Map.of();

        Map<Long, List<StoreResDTO.SearchedStoreMembership>> map = new HashMap<>();
        for (BrandMembershipProjection r : rows) {
            map.computeIfAbsent(r.getStoreBrandId(), k -> new ArrayList<>())
                    .add(StoreResDTO.SearchedStoreMembership.builder()
                            .id(r.getMembershipId())
                            .name(r.getMembershipName())
                            .logoUrl(r.getMembershipLogoUrl())
                            .build());
        }
        return map;
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
                        .logoUrl(m.getLogoUrl())
                        .build())
                .toList();
    }

    private Optional<MembershipBrand> resolveMembership(String query) {
        String nq = StoreUtil.searchNormalize(query);
        if (nq == null || nq.isBlank()) return Optional.empty();


        Optional<MembershipBrand> exact = membershipBrandAliasRepository.findMembershipByNormalizedAlias(nq);
        if (exact.isPresent()) return exact;

        List<MembershipBrand> candidates = membershipBrandAliasRepository.findMembershipsContainedInQuery(nq);
        return candidates.isEmpty() ? Optional.empty() : Optional.of(candidates.get(0));
    }

    private Optional<StoreBrand> resolveStoreBrand(String query) {
        String nq = StoreUtil.searchNormalize(query);
        if (nq == null || nq.isBlank()) return Optional.empty();

        Optional<StoreBrand> exact = storeBrandAliasRepository.findBrandByNormalizedAlias(nq);
        if (exact.isPresent()) return exact;

        List<StoreBrand> candidates = storeBrandAliasRepository.findBrandsContainedInQuery(nq);
        return candidates.isEmpty() ? Optional.empty() : Optional.of(candidates.get(0));
    }
}
