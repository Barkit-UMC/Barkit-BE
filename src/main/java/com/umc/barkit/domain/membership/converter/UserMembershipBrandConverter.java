package com.umc.barkit.domain.membership.converter;

import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMembershipBrandConverter {

    private final MembershipBrandRepository membershipBrandRepository;

    public UserMembershipBrandResponseDTO.SearchResultDTO toSearchResultDTO(
            List<UserMembershipBrand> userBrands,
            Long nextCursor,
            Boolean hasNext
    ) {
        List<UserMembershipBrandResponseDTO.UserBrandDTO> brandDTOs = userBrands.stream()
                .map(this::toUserBrandDTO)
                .collect(Collectors.toList());

        return UserMembershipBrandResponseDTO.SearchResultDTO.builder()
                .brands(brandDTOs)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

    public UserMembershipBrandResponseDTO.UserBrandDTO toUserBrandDTO(UserMembershipBrand userBrand) {
        MembershipBrand brand = membershipBrandRepository
                .findById(userBrand.getMembershipBrandId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BRAND4001));

        return UserMembershipBrandResponseDTO.UserBrandDTO.builder()
                .userMembershipBrandId(userBrand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .build();
    }

    public static UserMembershipBrand toUserMembershipBrand(
            Long userId,
            Long membershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    ) {
        return UserMembershipBrand.builder()
                .userId(userId)
                .membershipBrandId(membershipBrandId)
                .membershipNumber(request.getMembershipNumber())
                .isMain(false)
                .build();
    }

    public static UserMembershipBrandResponseDTO.RegisterMembershipResultDTO toRegisterMembershipResultDTO(
            UserMembershipBrand userMembershipBrand
    ) {
        return UserMembershipBrandResponseDTO.RegisterMembershipResultDTO.builder()
                .userMembershipBrandId(userMembershipBrand.getId())
                .membershipNumber(userMembershipBrand.getMembershipNumber())
                .build();
    }
}