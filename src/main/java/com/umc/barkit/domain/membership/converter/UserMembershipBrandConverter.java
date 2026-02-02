package com.umc.barkit.domain.membership.converter;


import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.exception.MembershipException;
import com.umc.barkit.domain.membership.exception.code.MembershipErrorCode;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;

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
        // MembershipBrand 조회
        MembershipBrand brand = membershipBrandRepository
                .findById(userBrand.getMembershipBrandId())
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BRAND4001));

        return UserMembershipBrandResponseDTO.UserBrandDTO.builder()
                .userMembershipBrandId(userBrand.getId())
                .name(brand.getName())
                .logoUrl(brand.getLogoUrl())
                .build();
    }

    // Request → Entity
    public static UserMembershipBrand toUserMembershipBrand(
            Long userId,
            Long membershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    ) {
        return UserMembershipBrand.builder()
                .userId(userId)
                .membershipBrandId(membershipBrandId)
                .membershipNumber(request.getMembershipNumber())
                .barcodeRawValue(request.getBarcodeRawValue())
                .isMain(false)
                .build();
    }

    // Entity → Response DTO
    public static UserMembershipBrandResponseDTO.RegisterMembershipResultDTO toRegisterMembershipResultDTO(
            UserMembershipBrand userMembershipBrand
    ) {
        return UserMembershipBrandResponseDTO.RegisterMembershipResultDTO.builder()
                .userMembershipBrandId(userMembershipBrand.getId())
                .membershipNumber(userMembershipBrand.getMembershipNumber())
                .barcodeRawValue(userMembershipBrand.getBarcodeRawValue())
                .build();
    }

    //MembershipBrand Entity List -> BarcodeDTO 변환
    public UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO toBarcodeDTO(UserMembershipBrand umb){

        MembershipBrand brand = membershipBrandRepository.findById(umb.getMembershipBrandId())
                .orElseThrow(() -> new MembershipException(MembershipErrorCode.BRAND4001));

        return UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO.builder()
                .membershipNumber(umb.getMembershipNumber())
                .barcodeRawValue(umb.getBarcodeRawValue())
                .logoUrl(brand.getLogoUrl())
                .brandName(brand.getName())
                .build();
    }
}







