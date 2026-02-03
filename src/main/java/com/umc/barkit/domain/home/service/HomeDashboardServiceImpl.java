package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeDashboardServiceImpl implements HomeDashboardService {

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public HomeDashboardResponse.DashboardDTO getDashboard() {

        Long userId = getCurrentUserId();

        /**
         * 1 사용자 보유 멤버십 조회
         * - 대표 멤버십(isMain = true) 우선
         * - 이후 등록순(createdAt ASC)
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
         * 2. MembershipBrand 조회 (ID → Entity 매핑)
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
         * 3. 정렬된 userMembership 순서를 유지한 채 DTO 변환
         */
        List<HomeDashboardResponse.MembershipSummaryDTO> membershipDTOs =
                userMemberships.stream()
                        .map(userMembership -> {
                            MembershipBrand brand =
                                    membershipBrandMap.get(userMembership.getMembershipBrandId());

                            return HomeDashboardResponse.MembershipSummaryDTO.builder()
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

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUserId();
    }
}
