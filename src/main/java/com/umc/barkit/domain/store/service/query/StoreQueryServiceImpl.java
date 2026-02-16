package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandAliasRepository;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import com.umc.barkit.domain.store.dto.google.GooglePlaceDTO;
import com.umc.barkit.domain.store.dto.req.StoreReqDTO;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.external.google.GoogleMapSearchClient;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import com.umc.barkit.domain.store.external.google.service.GoogleSearchCacheService;
import com.umc.barkit.domain.store.repository.StoreBrandAliasRepository;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandRepository;
import com.umc.barkit.domain.store.repository.projection.BrandIdNameProjection;
import com.umc.barkit.domain.store.repository.projection.BrandMembershipProjection;
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

    private static final double MAX_DISTANCE_KM = 5.0;

    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreBrandRepository storeBrandRepository;
    private final StoreBrandAliasRepository storeBrandAliasRepository;
    private final MembershipBrandAliasRepository membershipBrandAliasRepository;
    private final UserMembershipBrandRepository userMembershipBrandRepository;

    private final GoogleMapSearchClient googleClient;
    private final GoogleSearchCacheService googleSearchCacheService;

    @Qualifier("googleSearchExecutor")
    private final Executor googleSearchExecutor;

    //목록 조회(멤버십/매장)
    @Override
    public StoreResDTO.SearchedStoreSlice search(
            StoreReqDTO.SearchReq req,
            DistanceType distanceType,
            Category category,
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
        sortInPlace(deduped);

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

    @Transactional(readOnly = true)
    @Override
    public StoreResDTO.StoreDetail detail(StoreReqDTO.DetailReq req, Long userId) {

        log.info("[DETAIL CALL] googleId={}", req.googleId());

        // Google Places API 호출
        GooglePlaceDTO.Place g = googleSearchCacheService.getPlaceDetail(req.googleId());

        // 위치 계산
        double storeLat = g.location().latitude();
        double storeLng = g.location().longitude();

        StoreResDTO.StoreLocation location = StoreResDTO.StoreLocation.builder()
                .lat(storeLat)
                .lng(storeLng)
                .build();

        double distance = StoreUtil.round2(StoreUtil.distanceKm(req.userLat(), req.userLng(), storeLat, storeLng));

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


        List<Long> membershipIds = (req.membershipIds() == null) ? List.of() : req.membershipIds();

        if (membershipIds.isEmpty()) {
            return StoreResDTO.StoreDetail.builder()
                    .name(g.displayName() != null ? g.displayName().text() : "이름 정보 없음")
                    .distance(distance)
                    .location(location)
                    .contact(contact)
                    .hourInfo(hourInfo)
                    .membership(List.of())
                    .userMembership(List.of())
                    .photos(photos)
                    .build();
        }

        // 적용가능 전체 멤버십
        Map<Long, MembershipBrand> brandMap = membershipBrandRepository.findAllById(membershipIds).stream()
                .collect(Collectors.toMap(MembershipBrand::getId, b -> b));

        List<StoreResDTO.MembershipInfo> membershipInfos = membershipIds.stream()
                .map(id -> brandMap.get(id))
                .filter(Objects::nonNull)
                .map(b -> StoreResDTO.MembershipInfo.builder()
                        .name(b.getName())
                        .logoUrl(b.getLogoUrl())
                        .build())
                .toList();

        // 유저 보유 멤버십 조회
        List<UserMembershipBrand> userMembershipEntities = userMembershipBrandRepository.findByUserId(userId);

        Map<Long, Long> brandIdToUserMembershipId = userMembershipEntities.stream()
                .collect(Collectors.toMap(
                        UserMembershipBrand::getMembershipBrandId,
                        UserMembershipBrand::getId,
                        (a, b) -> a // 혹시 중복 대비
                ));

        List<StoreResDTO.UserMembershipInfo> userMembershipInfos = membershipIds.stream()
                .filter(brandIdToUserMembershipId::containsKey)
                .map(brandId -> {
                    MembershipBrand b = brandMap.get(brandId);
                    if (b == null) return null;

                    return StoreResDTO.UserMembershipInfo.builder()
                            .userMembershipId(brandIdToUserMembershipId.get(brandId))
                            .membershipBrandId(brandId)
                            .name(b.getName())
                            .logoUrl(b.getLogoUrl())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        // 8) DTO 조립
        return StoreResDTO.StoreDetail.builder()
                .name(g.displayName() != null ? g.displayName().text() : "이름 정보 없음")
                .distance(distance)
                .location(location)
                .contact(contact)
                .hourInfo(hourInfo)
                .membership(membershipInfos)
                .userMembership(userMembershipInfos)
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

    private void sortInPlace(List<StoreResDTO.SearchedStore> searchedStores) {
        if (searchedStores == null || searchedStores.size() <= 1) return;

        searchedStores.sort(Comparator.comparing(StoreResDTO.SearchedStore::distanceKm,
                Comparator.nullsLast(Double::compareTo)));
    }

    private List<StoreResDTO.SearchedStore> mapPlaces(
            StoreBrand matchedBrand,
            List<GoogleResDTO.Place> places,
            Double userLat,
            Double userLng,
            Double sendLat,
            Double sendLng,
            boolean applyRadiusFilter,
            List<StoreResDTO.SearchedStoreMembership> membershipsDTO
    ) {
        if (places == null || places.isEmpty()) return List.of();

        List<Long> membershipIds = (membershipsDTO == null) ? List.of()
                : membershipsDTO.stream()
                .map(StoreResDTO.SearchedStoreMembership::id)
                .filter(Objects::nonNull)
                .toList();

        return places.stream()
                .map(p -> {
                    String googleId = p.id();
                    GoogleResDTO.DisplayName name = p.displayName();

                    double storeLat = p.location().latitude();
                    double storeLng = p.location().longitude();

                    if (applyRadiusFilter) {
                        double distFromCenter = StoreUtil.distanceKm(sendLat, sendLng, storeLat, storeLng);
                        if (distFromCenter > MAX_DISTANCE_KM) return null;
                    }

                    double distanceKm = StoreUtil.round2(
                            StoreUtil.distanceKm(userLat, userLng, storeLat, storeLng)
                    );

                    return StoreResDTO.SearchedStore.builder()
                            .googleId(googleId)
                            .name(name)
                            .location(StoreResDTO.SearchedStoreLocation.builder()
                                    .lat(storeLat)
                                    .lng(storeLng)
                                    .build())
                            .address(p.formattedAddress())
                            .phone(p.nationalPhoneNumber())
                            .memberships(membershipsDTO)
                            .membershipIds(membershipIds)
                            .distanceKm(distanceKm)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
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

        // 검색 반경 5km 필터 적용
        List<GoogleResDTO.Place> places = googleSearchCacheService.searchNear(query, sendLat, sendLng);
        if (places == null || places.isEmpty()) return List.of();

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                findMembershipsForStoreBrand(matchedBrand.getId());

        return mapPlaces(
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
        // 검색 반경 5km 필터 미적용
        List<GoogleResDTO.Place> places = googleSearchCacheService.searchGlobal(query);
        if (places == null || places.isEmpty()) return List.of();

        List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                findMembershipsForStoreBrand(matchedBrand.getId());

        return mapPlaces(
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


    // 멤버십명 검색 로직
    private List<StoreResDTO.SearchedStore> searchByMembership(
            MembershipBrand membership,
            DistanceType distanceType,
            Category category,
            Double userLat,
            Double userLng,
            Double centerLat,
            Double centerLng
    ) {
        List<Long> storeBrandIds =
                storeBrandMembershipBrandRepository.findStoreBrandIdsByMembershipBrandId(membership.getId());
        if (storeBrandIds == null || storeBrandIds.isEmpty()) return List.of();

        List<BrandIdNameProjection> idNames =
                storeBrandRepository.findIdNameByIdsAndCategory(storeBrandIds, category);
        if (idNames == null || idNames.isEmpty()) return List.of();

        List<Long> filteredBrandIds = idNames.stream().map(BrandIdNameProjection::getId).toList();
        List<String> storeNames = idNames.stream().map(BrandIdNameProjection::getName).toList();

        Map<Long, StoreBrand> storeBrandMap =
                storeBrandRepository.findAllById(filteredBrandIds).stream()
                        .collect(Collectors.toMap(StoreBrand::getId, sb -> sb));

        Map<Long, List<StoreResDTO.SearchedStoreMembership>> membershipsByBrandId =
                preloadMembershipsDTO(filteredBrandIds);

        Double sendLat = (distanceType == DistanceType.CURRENT) ? userLat : centerLat;
        Double sendLng = (distanceType == DistanceType.CURRENT) ? userLng : centerLng;

        List<CompletableFuture<GooglePlaceDTO.BrandPlacesResult>> futures = new ArrayList<>();

        for (int i = 0; i < storeNames.size(); i++) {
            String storeName = storeNames.get(i);
            Long storeBrandId = filteredBrandIds.get(i);

            futures.add(CompletableFuture.supplyAsync(() -> {
                        List<GoogleResDTO.Place> places = googleSearchCacheService.searchNear(storeName, sendLat, sendLng);
                        return new GooglePlaceDTO.BrandPlacesResult(storeBrandId, storeName, places);
                    }, googleSearchExecutor)
                    .orTimeout(3, TimeUnit.SECONDS)
                    .exceptionally(ex -> new GooglePlaceDTO.BrandPlacesResult(storeBrandId, storeName, List.of())));
        }

        List<GooglePlaceDTO.BrandPlacesResult> brandResults = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<StoreResDTO.SearchedStore> result = new ArrayList<>();

        for (GooglePlaceDTO.BrandPlacesResult r : brandResults) {
            if (r.places() == null || r.places().isEmpty()) continue;

            StoreBrand sb = storeBrandMap.get(r.storeBrandId());
            if (sb == null) continue;

            List<StoreResDTO.SearchedStoreMembership> membershipsDTO =
                    membershipsByBrandId.getOrDefault(r.storeBrandId(), List.of());

            result.addAll(mapPlaces(
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
