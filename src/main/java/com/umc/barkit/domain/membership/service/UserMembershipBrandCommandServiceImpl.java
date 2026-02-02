package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.UserMembershipBrandConverter;
import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
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
}