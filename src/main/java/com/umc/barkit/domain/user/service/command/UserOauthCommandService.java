package com.umc.barkit.domain.user.service.command;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserOauth;
import com.umc.barkit.domain.user.enums.AuthProvider;
import com.umc.barkit.domain.user.exception.UserException;
import com.umc.barkit.domain.user.exception.code.UserErrorCode;
import com.umc.barkit.domain.user.oauth.client.KakaoApiClient;
import com.umc.barkit.domain.user.oauth.client.NaverApiClient;
import com.umc.barkit.domain.user.oauth.config.KakaoOAuthProperties;
import com.umc.barkit.domain.user.oauth.config.NaverOauthProperties;
import com.umc.barkit.domain.user.repository.UserOauthRepository;
import com.umc.barkit.domain.user.repository.UserRepository;
import com.umc.barkit.global.auth.oauth.OAuthStateUtil;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserOauthCommandService {
    private final UserRepository userRepository;
    private final UserOauthRepository userOauthRepository;

    private final KakaoApiClient kakaoApiClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    private final NaverApiClient naverApiClient;
    private final NaverOauthProperties naverOauthProperties;
    private final OAuthStateUtil oAuthStateUtil;


    /**
     * [연동 상태 조회]
     * @param userId - 현재 로그인한 유저의 user_oauth를 조회
     * @return
     * - kakao 연결 여부/이메일/연결시각
     * - naver 연결 여부/이메일/연결시각
     */
    @Transactional(readOnly = true)
    public UserResponseDto.OAuthStatusResponseDto getStatus(Long userId) {
        // userId로 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // provider별 연결 여부 확인
        var kakao = statusOf(user, AuthProvider.KAKAO);
        var naver = statusOf(user, AuthProvider.NAVER);

        return new UserResponseDto.OAuthStatusResponseDto(kakao, naver);
    }

    private UserResponseDto.OAuthProviderStatusDto statusOf(User user, AuthProvider provider) {
        return userOauthRepository.findByUserAndProviderAndDisconnectedAtIsNull(user, provider)
                .map(o -> new UserResponseDto.OAuthProviderStatusDto(true, o.getProviderEmail(), o.getConnectedAt()))
                .orElseGet(() -> new UserResponseDto.OAuthProviderStatusDto(false, null, null));
    }

    /**
     * [카카오 연동]
     * - 프론트가 카카오 authorize를 거쳐 얻은 code를 전달
     * - 백엔드는 code->token->userInfo로 providerUid/email을 얻고
     * - 현재 로그인 userId에 연결
     */
    @Transactional
    public void connectKakao(Long userId, UserRequestDto.KakaoLoginRequestDto dto) {
        // 허용된 redirectUri만 받기
        if (kakaoOAuthProperties.redirectUriAllowlist() == null
                || !kakaoOAuthProperties.redirectUriAllowlist().contains(dto.redirectUri())) {
            throw new UserException(UserErrorCode.INVALID_REDIRECT_URI);
        }

        // 현재 로그인 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // 코드로 토큰 발급
        var token = kakaoApiClient.exchangeToken(dto.code(), dto.redirectUri());

        // accessTokeon으로 카카오 유저 정보 조회
        var userInfo = kakaoApiClient.getUserInfo(token.accessToken());

        // 카카오 고유 사용자 ID
        String providerUid = String.valueOf(userInfo.id());
        // 카카오 계정 이메일
        String email = userInfo.kakaoAccount() != null ? userInfo.kakaoAccount().email() : null;

        // 필수값 검증
        if (providerUid == null || providerUid.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_PROVIDER_UID_REQUIRED);
        }
        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_EMAIL_REQUIRED);
        }

        // DB 연결 처리
        connectInternal(user, AuthProvider.KAKAO, providerUid, email);
    }

    /**
     * [네이버 연동]
     * - state 추가 검증
     */
    @Transactional
    public void connectNaver(Long userId, UserRequestDto.NaverLoginRequestDto dto) {
        // 허용된 redirectUri만 받기
        if (naverOauthProperties.redirectUriAllowlist() == null
                || !naverOauthProperties.redirectUriAllowlist().contains(dto.redirectUri())) {
            throw new UserException(UserErrorCode.INVALID_REDIRECT_URI);
        }

        // state 위변조/만료 검증
        try {
            oAuthStateUtil.verify(naverOauthProperties.stateSecret(), dto.state());
        } catch (IllegalArgumentException e) {
            if ("STATE_EXPIRED".equals(e.getMessage())) {
                throw new UserException(UserErrorCode.OAUTH_STATE_EXPIRED);
            }
            throw new UserException(UserErrorCode.OAUTH_STATE_INVALID);
        }

        // 현재 로그인 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.NOT_FOUND));

        // code + state + redirectUri로 토큰 발급
        var token = naverApiClient.exchangeToken(dto.code(), dto.state(), dto.redirectUri());
        // accessToken으로 유저정보 조회
        var userInfo = naverApiClient.getUserInfo(token.accessToken());

        // 네이버 고유 사용자 ID
        String providerUid = userInfo.response() != null ? userInfo.response().id() : null;
        // 네이버 이메일
        String email = userInfo.response() != null ? userInfo.response().email() : null;

        // 필수값 검증
        if (providerUid == null || providerUid.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_PROVIDER_UID_REQUIRED);
        }
        if (email == null || email.isBlank()) {
            throw new UserException(UserErrorCode.OAUTH_EMAIL_REQUIRED);
        }

        // DB 연결 처리
        connectInternal(user, AuthProvider.NAVER, providerUid, email);
    }

    /**
     * [연동 처리 공통 로직]
     * - 한 유저는 provider당 1개 연동만 허용
     * - providerUid는 "다른 유저"에 활성 연결되어 있으면 연동 불가
     * - 과거에 끊긴 row가 있으면 reconnect로 재사용(유니크 충돌 방지)
     */
    private void connectInternal(User user, AuthProvider provider, String providerUid, String providerEmail) {
        // 내 계정이 이미 해당 provider를 연동 중인지 체크
        userOauthRepository.findByUserAndProviderAndDisconnectedAtIsNull(user, provider)
                .ifPresent(existing -> {
                    // 이미 같은 소셜 계정(providerUid)이면 그냥 OK
                    if (existing.getProviderUid().equals(providerUid)) {
                        return;
                    }
                    // 이미 연동되어 있는데 다른 providerUid면 정책상 불가
                    throw new UserException(UserErrorCode.OAUTH_ALREADY_CONNECTED);
                });

        // 이 providerUid가 다른 계정에 "활성 연결" 되어 있는지 체크
        userOauthRepository.findByProviderAndProviderUidAndDisconnectedAtIsNull(provider, providerUid)
                .ifPresent(existing -> {
                    if (!existing.getUser().getId().equals(user.getId())) {
                        throw new UserException(UserErrorCode.OAUTH_ALREADY_LINKED_TO_OTHER_USER);
                    }
                });

        // 기존 row가 있으면 재사용 / 없으면 새로 생성
        UserOauth entity = userOauthRepository.findByProviderAndProviderUid(provider, providerUid)
                .map(existing -> {
                    // // 과거에 끊긴 상태면 되살림
                    if (existing.isDisconnected()) {
                        existing.reconnect(user, providerEmail);
                    }
                    return existing;
                })
                .orElseGet(() -> UserOauth.builder()
                        .user(user)
                        .provider(provider)
                        .providerUid(providerUid)
                        .providerEmail(providerEmail)
                        .connectedAt(LocalDateTime.now())
                        .build()
                );

        userOauthRepository.save(entity);
    }
}
