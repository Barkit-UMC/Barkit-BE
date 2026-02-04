package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeDashboardServiceImpl implements HomeDashboardService {

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public HomeDashboardResponse.DashboardDTO getDashboard(Long userId) {

        /**
         * 사용자 보유 멤버십 정렬 규칙
         *
         * 1. 대표 멤버십(isMain = true) 우선 노출
         * 2. 동일 조건 내에서는 등록순(createdAt ASC)
         *
         * → 홈 화면에서 대표 멤버십을 가장 먼저 보여주기 위한 UX 정책
         */
        List<UserMembershipBrand> userMemberships =
                userMembershipBrandRepository.findByUserId(userId)
                        .stream()
                        .sorted(
                                Comparator
                                        .comparing(
                                                UserMembershipBrand::getIsMain,
                                                Comparator.nullsLast(Boolean::compareTo)
                                        ).reversed()
                                        .thenComparing(
                                                UserMembershipBrand::getCreatedAt,
                                                Comparator.nullsLast(Comparator.naturalOrder())
                                        )
                        )
                        .toList();

        /**
         * MembershipBrand 조회 (ID → Entity 매핑)
         */
        Map<Long, MembershipBrand> membershipBrandMap =
                membershipBrandRepository.findAllById(
                                userMemberships.stream()
                                        .map(UserMembershipBrand::getMembershipBrandId)
                                        .toList()
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                MembershipBrand::getId,
                                brand -> brand
                        ));

        /**
         * DTO 변환
         */
        List<HomeDashboardResponse.MembershipSummaryDTO> membershipDTOs =
                userMemberships.stream()
                        .map(userMembership -> {
                            MembershipBrand brand =
                                    membershipBrandMap.get(userMembership.getMembershipBrandId());

                            return HomeDashboardResponse.MembershipSummaryDTO.builder()
                                    .userMembershipBrandId(userMembership.getId()) // 추가됨
                                    .membershipBrandId(brand.getId())
                                    .name(brand.getName())
                                    .logoUrl(brand.getLogoUrl())
                                    .build();
                        })
                        .toList();

        return HomeDashboardResponse.DashboardDTO.builder()
                .memberships(membershipDTOs)
                .build();
    }
}
