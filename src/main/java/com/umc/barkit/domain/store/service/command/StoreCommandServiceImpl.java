package com.umc.barkit.domain.store.service.command;

import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.domain.store.util.StoreUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreCommandServiceImpl implements StoreCommandService{

    private final StoreRepository storeRepository;
    private final StoreUtil storeUtil;

    private static final double MAX_DISTANCE_KM = 5.0;

    @Override
    public List<StoreResDTO.SearchedStore> saveAndMap(
            StoreBrand storeBrand,
            List<GoogleResDTO.Place> places,
            Double userLat,
            Double userLng,
            Double sendLat,
            Double sendLng,
            boolean applyDistanceFilter,
            List<StoreResDTO.SearchedStoreMembership> membershipsDTO
    ) {
        if (places == null || places.isEmpty()) return List.of();
        if (storeBrand == null) return List.of();

        // placeId 중복 제거 + location 없는 데이터 제거
        Map<String, GoogleResDTO.Place> uniquePlacesById = new LinkedHashMap<>();
        for (GoogleResDTO.Place p : places) {
            if (p == null || p.id() == null) continue;
            if (p.location() == null) continue;
            uniquePlacesById.putIfAbsent(p.id(), p);
        }
        if (uniquePlacesById.isEmpty()) return List.of();

        List<String> googleIds = new ArrayList<>(uniquePlacesById.keySet());

        // 기존 store 한 번에 조회
        Map<String, Store> existingByGoogleId =
                storeRepository.findAllByGoogleIdIn(googleIds).stream()
                        .collect(Collectors.toMap(Store::getGoogleId, Function.identity()));

        // 없는 것만 모아 saveAll
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

        // DTO 매핑 + 거리 필터
        List<StoreResDTO.SearchedStore> result = new ArrayList<>();
        for (String gid : googleIds) {
            GoogleResDTO.Place p = uniquePlacesById.get(gid);
            Store store = existingByGoogleId.get(gid);
            if (p == null || store == null) continue;

            double storeLat = p.location().latitude();
            double storeLng = p.location().longitude();

            if (applyDistanceFilter) {
                double distKmFromSendPoint = storeUtil.distanceKm(sendLat, sendLng, storeLat, storeLng);
                if (distKmFromSendPoint > MAX_DISTANCE_KM) continue;
            }

            result.add(mapToSearchedStore(p, store.getId(), membershipsDTO, userLat, userLng));
        }

        return result;
    }

    private StoreResDTO.SearchedStore mapToSearchedStore(
            GoogleResDTO.Place p,
            Long storeId,
            List<StoreResDTO.SearchedStoreMembership> memberships,
            Double userLat,
            Double userLng
    ) {
        double storeLat = p.location().latitude();
        double storeLng = p.location().longitude();

        Double distanceKm = storeUtil.round2(storeUtil.distanceKm(userLat, userLng, storeLat, storeLng));
        String directionUrl = storeUtil.buildGoogleDirectionUrl(p.id(), storeLat, storeLng);

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


}
