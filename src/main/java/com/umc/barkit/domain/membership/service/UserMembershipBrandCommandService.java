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

    /**
     * 대표 멤버십 설정 / 해제 기능
     *
     * - 특정 유저가 보유한 멤버십(UserMembershipBrand)에 대해
     *   대표 멤버십(isMain)을 ON/OFF 토글한다.
     * - 대표 멤버십은 최대 3개까지 설정 가능하다.
     *
     * @param userId 로그인한 사용자 ID
     * @param userMembershipBrandId 대표로 설정/해제할 유저 멤버십 ID
     */
    boolean updateMainMembership(Long userId, Long userMembershipBrandId);
}