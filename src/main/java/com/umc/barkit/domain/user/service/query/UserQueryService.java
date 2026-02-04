package com.umc.barkit.domain.user.service.query;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.EmailCheckResponseDto;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserSession;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.domain.user.repository.UserSessionRepository;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import com.umc.barkit.global.auth.jwt.JwtUtil;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복 확인
    public UserResponseDto.EmailCheckResponseDto checkEmailAvailability(String email) {
        boolean isAvailable = !userRepository.existsByEmail(email); // DB에서 해당 이메일이 존재하면 false 반환
        String message = isAvailable ? "사용 가능한 아이디입니다" : "사용 불가능한 아이디입니다";
        return new EmailCheckResponseDto(isAvailable, message);
    }

    // 로그인
    public UserResponseDto.LoginResponseDto login(
            UserRequestDto.@Valid LoginRequestDto dto
    ) {

        // User 조회
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())){
            throw new UserException(UserErrorCode.INVALID);
        }

        // JWT 토큰 발급용 UserDetails
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // 엑세스 토큰 발급
        String accessToken = jwtUtil.createAccessToken(userDetails);
        // 리프레쉬 토큰 발급
        String refreshToken = jwtUtil.createRefreshToken(userDetails);

        // 리프레쉬 토큰을 해시화하여 DB에 저장
        saveRefreshToken(user, refreshToken);

        // DTO 조립
        return UserConverter.toLoginDTO(user, accessToken, refreshToken);
    }

    // 리프레쉬 토큰을 해시화하여 DB에 저장
    private void saveRefreshToken(User user, String refreshToken) {
        String refreshTokenHash = jwtUtil.hashToken(refreshToken); // 리프레쉬 토큰 해시화
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(jwtUtil.getRefreshExpiration().toSeconds());  // refreshExpiration(설정값) 이후 만료

        // UserSession 객체 생성
        UserSession userSession = UserSession.builder()
                .user(user)
                .refreshTokenHash(refreshTokenHash)  // 해시화된 리프레쉬 토큰
                .rememberMe(false)  // rememberMe 기본값은 false
                .issuedAt(now)  // 발급 시각
                .expiresAt(expiresAt)  // 만료 시각
                .build();

        userSessionRepository.save(userSession); // DB에 저장
    }

    // 엑세스 토큰 재발급
    public String refreshAccessToken(String refreshToken) {
        // 요청 바디에 리프레쉬 토큰이 없으면 재발급 자체가 불가능
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        // JWT 서명/만료 등 기본 유효성 검증
        if (!jwtUtil.isValid(refreshToken)) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 리프레쉬 토큰의 subject(email)로 사용자 식별
        String email = jwtUtil.getEmail(refreshToken);
        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_INVALID);
        }

        // 이메일로 User 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 리프레쉬 토큰 해시화
        String refreshTokenHash = jwtUtil.hashToken(refreshToken);

        // 세션 존재 여부 확인
        UserSession session = userSessionRepository.findByUserAndRefreshTokenHash(user, refreshTokenHash)
                .orElseThrow(() -> new UserException(UserErrorCode.REFRESH_TOKEN_INVALID));

        // DB에 저장된 만료 시간 기준으로 세션 만료 여부 확인
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // 새 엑세스 토큰 발급
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.createAccessToken(userDetails);
    }

    // 개인정보 조회
    public UserResponseDto.PersonalInfoResponseDto getPersonalInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        if (user.getDeletedAt() != null) {
            throw new UserException(UserErrorCode.NOT_FOUND);
        }

        return UserConverter.toPersonalInfoDto(user);
    }
}
