package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomePopularStoreResponse;
import com.umc.barkit.domain.home.exception.HomeException;
import com.umc.barkit.domain.home.exception.code.HomeErrorCode;
import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.domain.store.repository.projection.BrandViewCountProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomePopularStoreServiceImpl implements HomePopularStoreService {

    private static final int PREVIEW_SIZE = 5;

    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreRepository storeRepository;

    @Override
    public HomePopularStoreResponse getPopularStores(Long membershipBrandId) {

        // 1️. 멤버십 → StoreBrand IDs
        List<Long> storeBrandIds =
                storeBrandMembershipBrandRepository
                        .findStoreBrandIdsByMembershipBrandId(membershipBrandId);

        //  유효하지 않은 멤버십
        if (storeBrandIds.isEmpty()) {
            throw new HomeException(HomeErrorCode.HOME4001);
        }

        // 2. viewCount 기준 인기 매장 상위 5개 조회
        List<BrandViewCountProjection> brands =
                storeRepository.findPopularBrandsByBrandIds(
                        storeBrandIds,
                        PageRequest.of(0, PREVIEW_SIZE)
                );

        // 3️. DTO 변환
        return HomePopularStoreResponse.fromBrands(brands);
    }
}
