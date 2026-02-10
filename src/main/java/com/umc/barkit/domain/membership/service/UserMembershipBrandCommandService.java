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

    /**
     * 멤버십 번호 변경
     *
     * @param userId 로그인한 사용자 ID
     * @param userMembershipBrandId 변경할 멤버십 ID
     * @param request 새로운 멤버십 번호
     * @return 업데이트된 멤버십 정보
     */
    UserMembershipBrandResponseDTO.RegisterMembershipResultDTO updateMembershipNumber(
            Long userId,
            Long userMembershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    );

    /**
     * 멤버십 삭제 (Hard Delete)
     *
     * @param userId 로그인한 사용자 ID
     * @param userMembershipBrandId 삭제할 멤버십 ID
     * @return 삭제된 멤버십 정보
     */
    UserMembershipBrandResponseDTO.RegisterMembershipResultDTO deleteMembership(
            Long userId,
            Long userMembershipBrandId
    );
}