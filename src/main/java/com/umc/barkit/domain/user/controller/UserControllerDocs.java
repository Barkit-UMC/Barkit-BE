package com.umc.barkit.domain.user.controller;

import com.umc.barkit.domain.user.dto.req.UserRequestDto;
import com.umc.barkit.domain.user.dto.res.UserResponseDto;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

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
}
