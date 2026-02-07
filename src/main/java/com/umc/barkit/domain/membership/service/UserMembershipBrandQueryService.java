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

    // 사용자 보유 특정 멤버십 상세 정보 조회
    UserMembershipBrandResponseDTO.MembershipDetailDTO getMembershipDetail(
            Long userId,
            Long userMembershipBrandId
    );
  
    UserMembershipBrandResponseDTO.AvailableStoreListDTO getAvailableStores(
            Long userId,
            Long userMembershipBrandId,
            String keyword,
            Long cursor,
            Integer size
    );

}
