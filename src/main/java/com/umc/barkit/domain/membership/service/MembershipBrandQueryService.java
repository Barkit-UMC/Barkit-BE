package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;

public interface MembershipBrandQueryService {

    // 기본 10개 멤버십 브랜드 조회
    MembershipBrandResponseDTO.DefaultBrandsDTO getDefaultMembershipBrands();

    // 인기 멤버십 브랜드 10개 조회
    MembershipBrandResponseDTO.PopularBrandsDTO getPopularMembershipBrands();

    // 멤버십 브랜드 검색 (커서 페이지네이션)
    MembershipBrandResponseDTO.SearchResultDTO searchMembershipBrands(
            String keyword,
            Long cursor,
            Integer limit
    );
}