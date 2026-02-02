package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;

public interface UserMembershipBrandCommandService {

    // 멤버십 번호로 등록
    UserMembershipBrandResponseDTO.RegisterMembershipResultDTO registerMembership(
            Long userId,
            Long membershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    );
}