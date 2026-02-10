package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.UserMembershipBrandConverter;
import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.exception.code.MembershipErrorCode;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import com.umc.barkit.domain.membership.exception.MembershipException;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserMembershipBrandCommandServiceImpl implements UserMembershipBrandCommandService {

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    @Transactional
    public UserMembershipBrandResponseDTO.RegisterMembershipResultDTO registerMembership(
            Long userId,
            Long membershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    ) {
        // 1. 유효성 검사
        validateMembershipNumber(request.getMembershipNumber());

        // 2. 브랜드 존재 여부 확인
        membershipBrandRepository.findById(membershipBrandId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.BRAND4001));

        // 3. 중복 체크
        if (userMembershipBrandRepository.existsByUserIdAndMembershipBrandId(userId, membershipBrandId)) {
            throw new GeneralException(GeneralErrorCode.MEMBERSHIP4005);
        }

        // 4. Entity 생성
        UserMembershipBrand userMembershipBrand = UserMembershipBrandConverter.toUserMembershipBrand(
                userId, membershipBrandId, request
        );

        // 5. DB 저장
        UserMembershipBrand saved = userMembershipBrandRepository.save(userMembershipBrand);

        // 6. 응답 DTO 변환 및 반환
        return UserMembershipBrandConverter.toRegisterMembershipResultDTO(saved);
    }

    // ===== 유효성 검사 =====
    private void validateMembershipNumber(String membershipNumber) {
        // 빈 값 체크
        if (membershipNumber == null || membershipNumber.trim().isEmpty()) {
            throw new GeneralException(GeneralErrorCode.MEMBERSHIP4001);
        }

        // 숫자만 체크
        if (!membershipNumber.matches("\\d+")) {
            throw new GeneralException(GeneralErrorCode.MEMBERSHIP4003);
        }

        // 최소 길이 체크 (12자)
        if (membershipNumber.length() < 12) {
            throw new GeneralException(GeneralErrorCode.MEMBERSHIP4004);
        }

        // 최대 길이 체크 (20자)
        if (membershipNumber.length() > 20) {
            throw new GeneralException(GeneralErrorCode.MEMBERSHIP4002);
        }
    }

    /**
     * 대표 멤버십 설정 / 해제
     *
     * - 유저가 보유한 멤버십인지 검증
     * - 현재 isMain 상태에 따라 ON / OFF 토글
     * - 대표 멤버십은 최대 3개까지 허용
     *
     * @param userId 로그인한 사용자 ID
     * @param userMembershipBrandId 유저 멤버십 ID
     */
    @Override
    @Transactional
    public boolean updateMainMembership(Long userId, Long userMembershipBrandId) {

        // 1. 유저 멤버십 조회 + 존재 여부 검증
        UserMembershipBrand userMembershipBrand = userMembershipBrandRepository
                .findById(userMembershipBrandId)
                .orElseThrow(() ->
                        new GeneralException(MembershipErrorCode.MEMBERSHIP4004)
                );
        // "등록된 사용자 멤버십이 존재하지 않습니다."

        // 2. 소유권 검증
        if (!userMembershipBrand.getUserId().equals(userId)) {
            throw new GeneralException(MembershipErrorCode.MEMBERSHIP4006);
            // "본인의 멤버십만 대표 멤버십으로 설정할 수 있습니다."
        }

        // 3. 현재 대표 멤버십 여부
        boolean isCurrentlyMain = Boolean.TRUE.equals(userMembershipBrand.getIsMain());

        // 4. 대표 멤버십 ON 시 → 개수 제한 체크
        if (!isCurrentlyMain) {
            int mainCount = userMembershipBrandRepository.countMainByUserId(userId);

            if (mainCount >= 3) {
                throw new GeneralException(MembershipErrorCode.MEMBERSHIP4007);
                // "대표 멤버십은 최대 3개까지 설정할 수 있습니다."
            }
        }

        // 5. 토글 처리
        boolean isNowMain = !isCurrentlyMain;
        userMembershipBrand.updateIsMain(isNowMain);

        // 6. 저장 (dirty checking으로 사실상 생략 가능)
        userMembershipBrandRepository.save(userMembershipBrand);

        // 7. 토글 결과 반환
        return isNowMain;
    }

    @Override
    @Transactional
    public UserMembershipBrandResponseDTO.RegisterMembershipResultDTO updateMembershipNumber(
            Long userId,
            Long userMembershipBrandId,
            UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    ) {
        // 1. 유효성 검사
        validateMembershipNumber(request.getMembershipNumber());

        // 2. UserMembershipBrand 조회
        UserMembershipBrand userMembershipBrand = userMembershipBrandRepository
                .findById(userMembershipBrandId)
                .orElseThrow(() -> new MembershipException(MembershipErrorCode.MEMBERSHIP4004));

        // 3. 본인 소유 확인
        if (!userMembershipBrand.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorCode.MEMBERSHIP4005);
        }

        // 4. 멤버십 번호 업데이트
        userMembershipBrand.updateMembershipNumber(request.getMembershipNumber());

        // 5. DB 저장
        UserMembershipBrand updated = userMembershipBrandRepository.save(userMembershipBrand);

        // 6. 응답 DTO 변환 및 반환
        return UserMembershipBrandConverter.toRegisterMembershipResultDTO(updated);
    }

    @Override
    @Transactional
    public UserMembershipBrandResponseDTO.RegisterMembershipResultDTO deleteMembership(
            Long userId,
            Long userMembershipBrandId
    ) {
        // 1. UserMembershipBrand 조회
        UserMembershipBrand userMembershipBrand = userMembershipBrandRepository
                .findById(userMembershipBrandId)
                .orElseThrow(() -> new MembershipException(MembershipErrorCode.MEMBERSHIP4004));

        // 2. 본인 소유 확인
        if (!userMembershipBrand.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorCode.MEMBERSHIP4005);
        }

        // 3. 삭제 전에 응답 DTO 먼저 생성
        UserMembershipBrandResponseDTO.RegisterMembershipResultDTO response =
                UserMembershipBrandConverter.toRegisterMembershipResultDTO(userMembershipBrand);

        // 4. DB에서 완전 삭제
        userMembershipBrandRepository.delete(userMembershipBrand);

        // 5. 미리 만들어둔 응답 반환
        return response;
    }
}