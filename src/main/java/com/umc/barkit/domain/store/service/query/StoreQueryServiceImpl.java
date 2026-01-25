package com.umc.barkit.domain.store.service.query;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.store.dto.res.StoreResDTO;
import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.enums.Category;
import com.umc.barkit.domain.store.enums.DistanceType;
import com.umc.barkit.domain.store.enums.Sort;
import com.umc.barkit.domain.store.external.google.GoogleMapSearchClient;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import com.umc.barkit.domain.store.external.kakao.KakaoMapSearchClient;
import com.umc.barkit.domain.store.external.kakao.dto.KakaoResDTO;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreQueryServiceImpl implements StoreQueryService{

    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreBrandRepository storeBrandRepository;
    private final KakaoMapSearchClient kakaoClient;
    private final GoogleMapSearchClient googleClient;

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
        } else {
            //검색 결과 없음 오류 발생
        }
        return List.of();
    }

    //멤버십명인지 검증
    private Boolean isMembershipBrand(String query) {
        if (membershipBrandRepository.existsByName(query)) {
            return true;
        } else {
            return false;
        }
    }

    //매장명인지 검증
    private Boolean isStoreBrand(String query) {
        return null;
    }

    //멤버십명 검색 로직
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

        //카테고리 구분
        if (category != Category.ALL) {
            for (Long storeBrandId : storeBrandIds) {
                Optional<String> storeName = storeBrandRepository.findNameByIdAndCategory(storeBrandId, category);
                if (storeName != null) {
                    storeNames.add(storeName.get());
                }
            }
        } else if (category == Category.ALL) {
            for (Long storeBrandId : storeBrandIds) {
                String storeName = storeBrandRepository.findNameById(storeBrandId);
                storeNames.add(storeName);
            }
        }


        //검색 기준(현재 위치 기준/지도 중심거리 기준)
        if (distanceType == DistanceType.CURRENT) {
            for (String storeName : storeNames) {
                List<KakaoResDTO.Document> documents =
                        kakaoClient.searchByKeyword(storeName, userLat, userLng);
            }
        } else if (distanceType == DistanceType.CENTER) {
            for (String storeName : storeNames) {
                List<KakaoResDTO.Document> documents =
                        kakaoClient.searchByKeyword(storeName, centerLat, centerLng);
            }
        }

        return null;
    }


    //매장명 검색 로직

    //Store 저장 로직
    private void saveStore(List<KakaoResDTO.Document> documents) {
        String googleId;

        for (KakaoResDTO.Document document : documents) {
            googleId = getGoogleId(document.place_name());
            Store.builder()
                    .kakaoId(document.id())
                    .googleId(googleId)
                    .build();
        }
    }

    private String getGoogleId(String kakaoPlaceName) {
        List<GoogleResDTO.Place> places = googleClient.searchText(kakaoPlaceName);
        return places.get(0).id();
    }

}
