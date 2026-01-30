package com.umc.barkit.domain.home.service;

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

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public HomeDashboardResponse.DashboardDTO getDashboard() {

        Long userId = getCurrentUserId();

        // 1️ 사용자 보유 멤버십 (ID만 조회)
        List<UserMembershipBrand> userMemberships =
                userMembershipBrandRepository.findByUserId(userId);

        // 2 MembershipBrand 조회
        List<MembershipBrand> membershipBrands =
                membershipBrandRepository.findAllById(
                        userMemberships.stream()
                                .map(UserMembershipBrand::getMembershipBrandId)
                                .toList()
                );

        // 3️ 멤버십 요약 DTO 변환
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
                .memberships(membershipDTOs)
                .build();
    }

    private Long getCurrentUserId() {
        // TODO: SecurityContext 연동
        return 100L;
    }
}
