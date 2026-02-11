package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
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

        List<UserMembershipBrand> userMemberships =
                userMembershipBrandRepository.findByUserId(userId);

        // MembershipBrand 매핑
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

        // 1. 대표 멤버십 (isMain = true)
        List<HomeDashboardResponse.MainMembershipDTO> mainMemberships =
                userMemberships.stream()
                        .filter(UserMembershipBrand::getIsMain)
                        .sorted(Comparator.comparing(
                                UserMembershipBrand::getCreatedAt,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .map(umb -> {
                            MembershipBrand brand =
                                    membershipBrandMap.get(umb.getMembershipBrandId());

                            return HomeDashboardResponse.MainMembershipDTO.builder()
                                    .userMembershipBrandId(umb.getId())
                                    .membershipBrandId(brand.getId())
                                    .name(brand.getName())
                                    .logoUrl(brand.getLogoUrl())
                                    .membershipNumber(umb.getMembershipNumber()) // 바코드용
                                    .build();
                        })
                        .toList();

        // 2. 전체 멤버십 (대표 멤버십 포함)
        List<HomeDashboardResponse.MembershipSummaryDTO> memberships =
                userMemberships.stream()
                        .sorted(Comparator.comparing(
                                UserMembershipBrand::getCreatedAt,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .map(umb -> {
                            MembershipBrand brand =
                                    membershipBrandMap.get(umb.getMembershipBrandId());

                            if (brand == null) {
                                throw new GeneralException(GeneralErrorCode.HOME4001);
                            }

                            return HomeDashboardResponse.MembershipSummaryDTO.builder()
                                    .userMembershipBrandId(umb.getId())
                                    .membershipBrandId(brand.getId())
                                    .name(brand.getName())
                                    .logoUrl(brand.getLogoUrl())
                                    .build();
                        })
                        .toList();

        return HomeDashboardResponse.DashboardDTO.builder()
                .mainMemberships(mainMemberships)
                .memberships(memberships)
                .build();
    }
}
