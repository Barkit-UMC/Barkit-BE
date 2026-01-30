package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus;
import com.umc.barkit.domain.favorite.repository.FavoriteStoreBrandRepository;
import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeDashboardServiceImpl implements HomeDashboardService {

    private final FavoriteStoreBrandRepository favoriteStoreBrandRepository;
    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public HomeDashboardResponse.DashboardDTO getDashboard() {

        Long userId = getCurrentUserId();

        // 즐겨찾기 매장 (ACTIVE, 최신 5개)
        List<FavoriteStoreBrand> favorites =
                favoriteStoreBrandRepository
                        .findTop5ByUser_IdAndStatusOrderByCreatedAtDesc(
                                userId,
                                FavoriteStoreStatus.ACTIVE
                        );

        // 사용자 보유 멤버십 (ID만)
        List<UserMembershipBrand> userMemberships =
                userMembershipBrandRepository.findByUserId(userId);

        // MembershipBrand 조회
        List<MembershipBrand> membershipBrands =
                membershipBrandRepository.findAllById(
                        userMemberships.stream()
                                .map(UserMembershipBrand::getMembershipBrandId)
                                .toList()
                );

        //  즐겨찾기 매장 DTO
        List<HomeDashboardResponse.FavoriteStoreDTO> favoriteStoreDTOs =
                favorites.stream()
                        .map(favorite -> HomeDashboardResponse.FavoriteStoreDTO.builder()
                                .favoriteId(favorite.getId())
                                .storeId(favorite.getStoreBrand().getId())
                                .storeName(favorite.getStoreBrand().getName())
                                .isBenefitAvailable(false) // TODO: 혜택 판단 QueryDSL
                                .build()
                        )
                        .toList();

        //  멤버십 요약 DTO
        List<HomeDashboardResponse.MembershipSummaryDTO> membershipDTOs =
                membershipBrands.stream()
                        .map(brand -> HomeDashboardResponse.MembershipSummaryDTO.builder()
                                .membershipBrandId(brand.getId())
                                .name(brand.getName())
                                .logoUrl(brand.getLogoUrl())
                                .build()
                        )
                        .toList();

        return HomeDashboardResponse.DashboardDTO.builder()
                .favoriteStores(favoriteStoreDTOs)
                .memberships(membershipDTOs)
                .build();
    }

    private Long getCurrentUserId() {
        // TODO SecurityContext 연동
        return 100L;
    }
}
