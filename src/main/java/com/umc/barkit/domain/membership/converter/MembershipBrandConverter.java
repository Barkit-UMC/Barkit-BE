package com.umc.barkit.domain.membership.converter;

import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;

import java.util.List;
import java.util.stream.Collectors;

public class MembershipBrandConverter {

    // MembershipBrand Entity -> BrandSimpleDTO 변환
    public static MembershipBrandResponseDTO.BrandSimpleDTO toBrandSimpleDTO(MembershipBrand membershipBrand) {
        return MembershipBrandResponseDTO.BrandSimpleDTO.builder()
                .membershipBrandId(membershipBrand.getId())
                .name(membershipBrand.getName())
                .logoUrl(membershipBrand.getLogoUrl())
                .build();
    }

    // MembershipBrand Entity List -> BrandSimpleDTO List 변환
    public static List<MembershipBrandResponseDTO.BrandSimpleDTO> toBrandSimpleDTOList(List<MembershipBrand> membershipBrands) {
        return membershipBrands.stream()
                .map(MembershipBrandConverter::toBrandSimpleDTO)
                .collect(Collectors.toList());
    }

    // MembershipBrand Entity List -> Top4BrandsDTO 변환
    public static MembershipBrandResponseDTO.Top4BrandsDTO toTop4BrandsDTO(List<MembershipBrand> membershipBrands) {
        List<MembershipBrandResponseDTO.BrandSimpleDTO> brandDTOs = toBrandSimpleDTOList(membershipBrands);

        return MembershipBrandResponseDTO.Top4BrandsDTO.builder()
                .brands(brandDTOs)
                .build();
    }

    // MembershipBrand Entity -> BrandNameOnlyDTO 변환
    public static MembershipBrandResponseDTO.BrandNameOnlyDTO toBrandNameOnlyDTO(MembershipBrand membershipBrand) {
        return MembershipBrandResponseDTO.BrandNameOnlyDTO.builder()
                .membershipBrandId(membershipBrand.getId())
                .name(membershipBrand.getName())
                .build();
    }

    // MembershipBrand Entity List -> BrandNameOnlyDTO List 변환
    public static List<MembershipBrandResponseDTO.BrandNameOnlyDTO> toBrandNameOnlyDTOList(List<MembershipBrand> membershipBrands) {
        return membershipBrands.stream()
                .map(MembershipBrandConverter::toBrandNameOnlyDTO)
                .collect(Collectors.toList());
    }

    // MembershipBrand Entity List -> PopularBrandsDTO 변환
    public static MembershipBrandResponseDTO.PopularBrandsDTO toPopularBrandsDTO(List<MembershipBrand> membershipBrands) {
        List<MembershipBrandResponseDTO.BrandNameOnlyDTO> brandDTOs = toBrandNameOnlyDTOList(membershipBrands);

        return MembershipBrandResponseDTO.PopularBrandsDTO.builder()
                .brands(brandDTOs)
                .build();
    }


    public static MembershipBrandResponseDTO.SearchResultDTO toSearchResultDTO(
            List<MembershipBrand> brands,
            Long nextCursor,
            Boolean hasNext
    ) {
        List<MembershipBrandResponseDTO.BrandSimpleDTO> brandDTOs = toBrandSimpleDTOList(brands);

        return MembershipBrandResponseDTO.SearchResultDTO.builder()
                .brands(brandDTOs)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build();
    }

}