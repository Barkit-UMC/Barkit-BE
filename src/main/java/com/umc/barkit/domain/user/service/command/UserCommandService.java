package com.umc.barkit.domain.user.service.command;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.entity.Term;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserOauth;
import com.umc.barkit.domain.user.entity.mapping.UserTerm;
import com.umc.barkit.domain.user.enums.Role;
import com.umc.barkit.domain.user.enums.UserStatus;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.repository.TermRepository;
import com.umc.barkit.domain.user.repository.UserOauthRepository;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.domain.user.repository.UserSessionRepository;
import com.umc.barkit.domain.user.repository.UserTermRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final TermRepository termRepository;
    private final UserRepository userRepository;
    private final UserTermRepository userTermRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionRepository userSessionRepository;
    private final UserOauthRepository userOauthRepository;

    // 회원가입
    @Transactional
    public UserResponseDto.SignupResponseDto signup(UserRequestDto.SignupRequestDto signupRequestDto){

        // 이메일 중복 확인
        if (userRepository.existsByEmailAndStatus(signupRequestDto.email(), UserStatus.ACTIVE)) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 비밀번호 확인
        if(!signupRequestDto.password().equals(signupRequestDto.confirmPassword())){
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDto.password());

        // User 생성
        User user = UserConverter.toUser(signupRequestDto, encodedPassword, Role.ROLE_USER);

        // User 저장
        userRepository.save(user);

        // Term 리스트 조회
        List<Term> terms = termRepository.findAll();

        // UserTerm 생성
        List<UserTerm> userTerms = UserConverter.toUserTermEntities(signupRequestDto.terms(), user, terms);
        userTermRepository.saveAll(userTerms);

        // DTO 응답
        return new UserResponseDto.SignupResponseDto(user.getId(), user.getEmail());
    }

    // 생년월일 변경
    @Transactional
    public void updateBirthDate(Long userId, LocalDate birthDate){
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        user.updateBirthDate(birthDate);
    }

    // 현재 비밀번호 검증
    @Transactional
    public void validateCurrentPassword(Long userId, UserRequestDto.ValidatePasswordRequestDto request) {
        // 사용자 조회
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 현재 비밀번호와 입력한 비밀번호 비교
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new UserException(UserErrorCode.INVALID_CURRENT_PASSWORD); // 비밀번호 불일치 시 예외 처리
        }
    }


    // 비밀번호 변경
    @Transactional
    public void updatePassword(Long userId, UserRequestDto.UpdatePasswordRequestDto request){
        // 현재 비밀번호 검증
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 현재 비밀번호와 입력한 비밀번호 비교
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new UserException(UserErrorCode.INVALID_CURRENT_PASSWORD); // 비밀번호 불일치
        }

        // 새 비밀번호와 확인 비밀번호 일치 여부
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH); // 새 비밀번호 불일치
        }

        if (request.newPassword().length() < 8 || request.newPassword().length() > 12) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD); // 비밀번호 길이 오류
        }

        if (!request.newPassword().matches(".*[a-zA-Z].*") || !request.newPassword().matches(".*[!@#$%^&*].*")) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD); // 영문자 + 특수문자 조합 오류
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(request.newPassword());

        // 비밀번호 업데이트
        user.updatePassword(encodedNewPassword);
    }

    // 알림 설정 변경
    @Transactional
    public void updateNotification(Long userId, Boolean enabled) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 1️. INACTIVE 유저 차단
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserException(UserErrorCode.INACTIVE_USER);
        }


        // 2. 동일 상태 요청 방어
        if (Boolean.TRUE.equals(user.getNotificationEnabled()) == enabled) {
            return;
        }

        user.updateNotificationEnabled(enabled);
    }

    // 위치 권한 동의 상태 변경
    @Transactional
    public void updateLocationConsent(Long userId, Boolean consented) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 1️. INACTIVE 유저 차단
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserException(UserErrorCode.INACTIVE_USER);
        }

        // 2️. 동일 상태 요청 방어
        if (user.getLocationConsent().equals(consented)) {
            return;
        }

        user.updateLocationConsent(consented);
    }

    // 회원 탈퇴
    @Transactional
    public UserResponseDto.WithdrawResponseDto withdraw(Long userId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        String anonymizedEmail = "deleted_" + user.getId() + "_" + UUID.randomUUID() + "@barkit.invalid";
        user.softDelete(anonymizedEmail, now);

        userSessionRepository.revokeAllActiveByUserId(userId, now);

        List<UserOauth> oauthList = userOauthRepository.findAllByUser_IdAndDisconnectedAtIsNull(userId);
        for (UserOauth oauth : oauthList) {
            String anonymizedUid = "deleted_" + userId + "_" + UUID.randomUUID();
            oauth.disconnect(anonymizedUid, now);
        }

        return new UserResponseDto.WithdrawResponseDto(user.getId(), user.getDeletedAt());
    }
}
