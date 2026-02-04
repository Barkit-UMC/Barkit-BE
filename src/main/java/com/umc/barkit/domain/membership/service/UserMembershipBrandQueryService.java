package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;

public interface UserMembershipBrandQueryService {

    // 사용자 보유 멤버십 브랜드 검색
    UserMembershipBrandResponseDTO.SearchResultDTO searchUserMembershipBrands(
            Long userId,
            String keyword,
            Long cursor,
            Integer limit
    );

    UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO getUserMembershipBarcode(Long userId,Long membershipBrandId);

    UserMembershipBrandResponseDTO.AvailableStoreListDTO getAvailableStores(
            Long userId,
            Long userMembershipBrandId,
            String keyword
    );

}
