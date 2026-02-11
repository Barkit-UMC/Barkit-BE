package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserControllerDocs {

    @Operation(
            summary = "아이디(이메일) 중복 확인 API",
            description = "이메일을 입력받아 해당 이메일의 사용 가능 여부를 확인합니다. 사용 가능한 이메일이면 true, 사용 불가능한 이메일이면 false를 메시지와 함께 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    ApiResponse<UserResponseDto.EmailCheckResponseDto> checkEmail(UserRequestDto.EmailCheckRequestDto emailDto);


    @Operation(
            summary = "회원가입 API",
            description = "사용자가 입력한 정보를 통해 새로운 회원을 등록합니다. 이메일, 비밀번호, 이름 등을 받아 회원가입을 진행하며, 성공하면 회원가입 완료된 사용자 정보를 반환합니다. \n" +
                    "회원가입 시, 3개의 약관에 대한 동의 여부 리스트를 반드시 포함해야 하며, 각 약관에 대해 사용자가 동의했는지 여부를 표시해야 합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "실패")
    })
    ApiResponse<UserResponseDto.SignupResponseDto> signup(UserRequestDto.SignupRequestDto signupRequestDto);


    @Operation(
            summary = "로그인 API",
            description = "사용자가 입력한 이메일과 비밀번호로 로그인한 후, JWT access token을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, JWT access token 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "잘못된 로그인 정보(이메일/비밀번호 불일치)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    })
    ApiResponse<UserResponseDto.LoginResponseDto> login(@RequestBody @Valid UserRequestDto.LoginRequestDto loginDto);


    @Operation(
            summary = "개인정보 조회 API",
            description = "개인정보 변경 페이지 진입 시, 사용자 개인정보(이름/이메일/전화번호/생년월일)를 조회합니다.<br>" +
                    "JWT 인증 방식으로 동작하며, Authorization 헤더의 Access Token에서 사용자 정보를 식별합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    })
    ApiResponse<UserResponseDto.PersonalInfoResponseDto> getPersonalInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    );


    @Operation(
            summary = "생년월일 변경 API",
            description = "개인정보 변경 페이지에서 사용자의 생년월일을 변경합니다.<br>" +
                    "JWT 인증 방식으로 동작하며, Authorization 헤더의 Access Token에서 사용자 정보를 식별합니다.<br>"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "변경 성공")
    })
    ApiResponse<UserResponseDto.PersonalInfoResponseDto> updateBirthDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.UpdateBirthDateRequestDto request
    );


    @Operation(
            summary = "비밀번호 변경 API",
            description = "JWT 인증된 사용자가 자신의 비밀번호를 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호 검증 실패 (현재 비밀번호 불일치, 새 비밀번호 불일치 등)")
    })
    ApiResponse<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.UpdatePasswordRequestDto request
    );

    @Operation(
            summary = "액세스 토큰 재발급 API",
            description = "Access Token 만료 시, Refresh Token을 이용해 새로운 Access Token을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레시 토큰 누락/유효하지 않음/만료")
    })
    ApiResponse<UserResponseDto.RefreshResponseDto> refresh(
            @RequestBody @Valid UserRequestDto.RefreshRequestDto request
    );

    @Operation(
            summary = "로그아웃 API",
            description = "로그아웃을 수행합니다.<br>" +
                    "서버는 Refresh Token을 무효화하여(세션 revoke 처리) 이후 Access Token 재발급을 차단합니다.<br>" +
                    "로그아웃 이후 동일한 Refresh Token으로는 Access Token 재발급이 불가능합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "리프레시 토큰 누락/유효하지 않음/만료/이미 무효화됨")
    })
    ApiResponse<Void> logout(
            @RequestBody @Valid UserRequestDto.RefreshRequestDto request
    );


    @Operation(
            summary = "알림 수신 설정 변경 API",
            description = "마이페이지에서 사용자의 알림 수신 여부를 변경합니다.<br>" +
                    "JWT 인증 방식으로 동작하며, 로그인한 사용자 본인의 설정만 변경할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "알림 설정 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    })
    ApiResponse<Void> updateNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.UpdateNotificationRequestDto request
    );

    @Operation(
            summary = "위치 권한 동의 상태 변경 API",
            description = "마이페이지에서 사용자의 위치 정보 제공 동의 여부를 변경합니다.<br>" +
                    "해당 설정은 위치 기반 서비스 제공 여부 판단에 사용됩니다.<br>" +
                    "JWT 인증 방식으로 동작하며, 로그인한 사용자 본인의 설정만 변경할 수 있습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "위치 권한 동의 상태 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    })
    ApiResponse<Void> updateLocationConsent(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserRequestDto.UpdateLocationConsentRequestDto request
    );

    @Operation(
            summary = "카카오 로그인 API",
            description = "프론트에서 카카오 인가 코드(code)와 redirectUri를 전달하면,<br>" +
                    "백엔드가 카카오 토큰 발급/사용자 정보 조회를 수행한 뒤 서비스 JWT(accessToken/refreshToken)를 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, 서비스 JWT(accessToken/refreshToken) 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "카카오 인증 실패/토큰 발급 실패")
    })
    ApiResponse<UserResponseDto.LoginResponseDto> kakaoLogin(
            @RequestBody @Valid UserRequestDto.KakaoLoginRequestDto request
    );

    @Operation(
            summary = "네이버 authorize URL 생성 API",
            description = "프론트에서 redirectUri를 전달하면, 백엔드가 state를 생성하고 네이버 로그인 페이지로 이동할 authorize URL을 반환합니다.<br>" +
                    "프론트는 반환된 URL로 이동하여 네이버 로그인/동의 화면을 진행합니다.<br>" +
                    "성공 후 프론트 redirectUri로 code/state가 전달됩니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "생성 성공 (네이버 authorize URL 반환)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "redirectUri 누락/형식 오류")
    })
    ApiResponse<String> naverAuthorizeUrl(
            @Parameter(description = "프론트 콜백 redirectUri", required = true,
                    example = "http://localhost:5173/oauth/naver/callback")
            @RequestParam String redirectUri
    );


    @Operation(
            summary = "네이버 로그인 API",
            description = "프론트에서 네이버 인가 코드(code), state, redirectUri를 전달하면,<br>" +
                    "백엔드가 state 검증 → 네이버 토큰 교환 → 사용자 정보 조회 → 서비스 JWT(accessToken/refreshToken) 발급을 수행합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공, 서비스 JWT(accessToken/refreshToken) 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 값 검증 실패 (code/state/redirectUri 누락 등)")
    })
    ApiResponse<UserResponseDto.LoginResponseDto> naverLogin(
            @RequestBody @Valid UserRequestDto.NaverLoginRequestDto request
    );

    @Operation(
            summary = "회원 탈퇴 API",
            description = """
                    로그인된 사용자의 계정을 탈퇴 처리합니다.<br>
                    <b>Soft Delete</b> 방식으로 처리되며, 사용자 상태를 비활성화하고 deletedAt을 기록합니다.<br>
                    또한, 사용자의 모든 활성 세션(Refresh Token)을 무효화하고, 탈퇴 후에는 기존 Access/Refresh Token으로 인증이 불가능합니다.
                    """
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 탈퇴 실패")
    })
    ApiResponse<UserResponseDto.WithdrawResponseDto> withdraw(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails userDetails
    );

}
