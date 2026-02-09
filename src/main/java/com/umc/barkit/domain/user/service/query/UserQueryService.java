package com.umc.barkit.domain.user.service.query;

import com.umc.barkit.domain.user.converter.UserConverter;
import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto.EmailCheckResponseDto;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserOauth;
import com.umc.barkit.domain.user.entity.UserSession;
import com.umc.barkit.domain.user.enums.AuthProvider;
import com.umc.barkit.domain.user.enums.Role;
import com.umc.barkit.domain.user.enums.UserStatus;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.oauth.client.KakaoApiClient;
import com.umc.barkit.domain.user.oauth.client.NaverApiClient;
import com.umc.barkit.domain.user.oauth.config.KakaoOAuthProperties;
import com.umc.barkit.domain.user.oauth.config.NaverOauthProperties;
import com.umc.barkit.domain.user.repository.UserOauthRepository;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.domain.user.repository.UserSessionRepository;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import com.umc.barkit.global.auth.jwt.JwtUtil;
import com.umc.barkit.global.auth.oauth.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final KakaoApiClient kakaoApiClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final UserOauthRepository userOauthRepository;
    private final NaverApiClient naverApiClient;
    private final NaverOauthProperties naverOauthProperties;
    private final OAuthStateUtil oAuthStateUtil;


    // 아이디 중복 확인
    public UserResponseDto.EmailCheckResponseDto checkEmailAvailability(String email) {
        boolean isAvailable = !userRepository.existsByEmailAndStatus(email, UserStatus.ACTIVE); // DB에서 해당 이메일이 존재하면 false 반환
        String message = isAvailable ? "사용 가능한 아이디입니다" : "사용 불가능한 아이디입니다";
        return new EmailCheckResponseDto(isAvailable, message);
    }

    // 로그인
    @Transactional
    public UserResponseDto.LoginResponseDto login(
            UserRequestDto.@Valid LoginRequestDto dto
    ) {

        // User 조회
        User user = userRepository.findByEmailAndStatus(dto.email(), UserStatus.ACTIVE)
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
    @Transactional
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
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 리프레쉬 토큰 해시화
        String refreshTokenHash = jwtUtil.hashToken(refreshToken);

        // 세션 존재 여부 확인
        UserSession session = userSessionRepository.findByUserAndRefreshTokenHashAndRevokedAtIsNull(user, refreshTokenHash)
                .orElseThrow(() -> new UserException(UserErrorCode.REFRESH_TOKEN_INVALID));

        // DB에 저장된 만료 시간 기준으로 세션 만료 여부 확인
        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        session.touch();

        // 새 엑세스 토큰 발급
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return jwtUtil.createAccessToken(userDetails);
    }

    // 개인정보 조회
    public UserResponseDto.PersonalInfoResponseDto getPersonalInfo(Long userId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        return UserConverter.toPersonalInfoDto(user);
    }

    // 로그아웃
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        if (!jwtUtil.isValid(refreshToken)) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_INVALID);
        }

        String email = jwtUtil.getEmail(refreshToken);
        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_INVALID);
        }

        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        String refreshTokenHash = jwtUtil.hashToken(refreshToken);

        UserSession session = userSessionRepository.findByUserAndRefreshTokenHashAndRevokedAtIsNull(user, refreshTokenHash)
                .orElseThrow(() -> new UserException(UserErrorCode.REFRESH_TOKEN_INVALID));

        session.revoke();
    }


    // 카카오 로그인
    @Transactional
    public UserResponseDto.LoginResponseDto kakaoLogin(UserRequestDto.KakaoLoginRequestDto dto) {
        // redirectUri 검증
        if (kakaoOAuthProperties.redirectUriAllowlist() == null
                || !kakaoOAuthProperties.redirectUriAllowlist().contains(dto.redirectUri())) {
            throw new UserException(UserErrorCode.INVALID_REDIRECT_URI);
        }

        // 인가코드(code)로 카카오 토큰 발급 요청
        var token = kakaoApiClient.exchangeToken(dto.code(), dto.redirectUri());
        // 카카오 access_token으로 유저 정보 조회
        var userInfo = kakaoApiClient.getUserInfo(token.accessToken());

        String providerUid = String.valueOf(userInfo.id()); // 카카오 유저 고유 id(
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;
        String nickname = userInfo.properties() != null ? userInfo.properties().nickname() : null;

        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_EMAIL_REQUIRED);
        }

        // (provider, providerUid)로 이미 연결된 유저가 있는지 먼저 확인
        //  - 있으면 해당 유저로 로그인 처리
        //  - 없으면 user 생성/조회 후 oauth 연결 저장
        User user = userOauthRepository
                .findByProviderAndProviderUidAndDisconnectedAtIsNull(AuthProvider.KAKAO, providerUid)
                .map(UserOauth::getUser)
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .orElseGet(() -> upsertUserAndConnectOauth(AuthProvider.KAKAO, email, nickname, providerUid));

        // JWT 발급
        return issueTokens(user);
    }

    private User upsertUserAndConnectOauth(AuthProvider provider, String email, String nickname, String providerUid) {
        // 이메일 기반으로 우리 서비스 유저가 이미 있으면 재사용
        User user = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseGet(() -> createSocialUser(email, nickname));

        // user_oauth에 (KAKAO, providerUid) 연결 정보 저장
        UserOauth oauth = UserOauth.builder()
                .user(user)
                .provider(provider)
                .providerUid(providerUid)
                .providerEmail(email)
                .connectedAt(LocalDateTime.now())
                .build();

        userOauthRepository.save(oauth);
        return user;
    }

    private User createSocialUser(String email, String nickname) {
        // name에 들어갈 표시 이름 결정 -  nickname을 name으로
        String displayName = (nickname != null && !nickname.isBlank()) ? nickname : email.split("@")[0];
        // 랜덤 비번 값
        String randomPw = java.util.UUID.randomUUID().toString();
        String encoded = passwordEncoder.encode(randomPw);

        User user = UserConverter.toSocialUser(
                email,
                displayName,
                encoded,
                Role.ROLE_USER
        );

        return userRepository.save(user);
    }

    private UserResponseDto.LoginResponseDto issueTokens(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // access/refresh 발급
        String accessToken = jwtUtil.createAccessToken(userDetails);
        String refreshToken = jwtUtil.createRefreshToken(userDetails);
        saveRefreshToken(user, refreshToken);

        // // 기존 로그인 응답 포맷
        return UserConverter.toLoginDTO(user, accessToken, refreshToken);
    }

    // 네이버 authorize URL 생성 메서드
    public String getNaverAuthorizeUrl(String redirectUri) {
        // redirectUri allowlist 검증
        if (naverOauthProperties.redirectUriAllowlist() == null
                || !naverOauthProperties.redirectUriAllowlist().contains(redirectUri)) {
            throw new UserException(UserErrorCode.INVALID_REDIRECT_URI);
        }

        // state 생성
        String state = oAuthStateUtil.generate(naverOauthProperties.stateSecret());
        return naverApiClient.buildAuthorizeUrl(redirectUri, state);
    }

    // 네이버 로그인
    @Transactional
    public UserResponseDto.LoginResponseDto naverLogin(UserRequestDto.NaverLoginRequestDto dto) {
        if (naverOauthProperties.redirectUriAllowlist() == null
                || !naverOauthProperties.redirectUriAllowlist().contains(dto.redirectUri())) {
            throw new UserException(UserErrorCode.INVALID_REDIRECT_URI);
        }

        try {
            oAuthStateUtil.verify(naverOauthProperties.stateSecret(), dto.state());
        } catch (IllegalArgumentException e) {
            if ("STATE_EXPIRED".equals(e.getMessage())) {
                throw new UserException(UserErrorCode.OAUTH_STATE_EXPIRED);
            }
            throw new UserException(UserErrorCode.OAUTH_STATE_INVALID);
        }

        var token = naverApiClient.exchangeToken(dto.code(), dto.state(), dto.redirectUri());
        var userInfo = naverApiClient.getUserInfo(token.accessToken());

        String providerUid = userInfo.response() != null ? userInfo.response().id() : null;
        String email = userInfo.response() != null ? userInfo.response().email() : null;
        String name = userInfo.response() != null ? userInfo.response().name() : null;

        if (providerUid == null || providerUid.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_PROVIDER_UID_REQUIRED);
        }

        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_EMAIL_REQUIRED);
        }

        User user = userOauthRepository
                .findByProviderAndProviderUidAndDisconnectedAtIsNull(AuthProvider.NAVER, providerUid)
                .map(UserOauth::getUser)
                .filter(u -> u.getStatus() == UserStatus.ACTIVE)
                .orElseGet(() -> upsertUserAndConnectOauth(AuthProvider.NAVER, email, name, providerUid));

        return issueTokens(user);
    }
}
