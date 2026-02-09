package com.umc.barkit.domain.membership.controller;


import com.umc.barkit.domain.membership.dto.request.UserMembershipBrandRequestDTO;
import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.exception.code.MembershipSuccessCode;
import com.umc.barkit.domain.membership.service.UserMembershipBrandCommandService;
import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.service.MembershipBrandQueryService;
import com.umc.barkit.domain.membership.service.UserMembershipBrandQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralErrorCode;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import com.umc.barkit.global.apiPayload.exception.GeneralException;
import com.umc.barkit.global.auth.details.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-membership-brands")
public class UserMembershipBrandController {

    private final UserMembershipBrandQueryService userMembershipBrandQueryService;
    private final UserMembershipBrandCommandService userMembershipBrandCommandService;

    @Operation(
            summary = "사용자 보유 멤버십 브랜드 검색",
            description = "로그인한 사용자가 보유한 멤버십 브랜드를 키워드로 검색합니다. " +
                    "대소문자 무시, 띄어쓰기/특수문자 무시, 커서 기반 페이지네이션을 지원합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.SearchResultDTO>> searchUserMembershipBrands(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,

            @Parameter(description = "커서", required = false, example = "0")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "한 번에 가져올 개수 (기본값: 20)", required = false, example = "20")
            @RequestParam(required = false) Integer limit
    ) {
        // JWT 인증 체크
        if (userDetails == null) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        Long userId = userDetails.getUserId();

        UserMembershipBrandResponseDTO.SearchResultDTO result =
                userMembershipBrandQueryService.searchUserMembershipBrands(userId, keyword, cursor, limit);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    @Operation(
            summary = "멤버십 등록",
            description = "사용자가 멤버십 번호를 직접 입력하거나 바코드 이미지를 통해 등록합니다."
    )
    @PostMapping("/{membershipBrandId}")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.RegisterMembershipResultDTO>> registerMembership(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long membershipBrandId,
            @RequestBody UserMembershipBrandRequestDTO.RegisterMembershipDTO request
    ) {
        // JWT 인증 체크
        if (userDetails == null) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        Long userId = userDetails.getUserId();

        UserMembershipBrandResponseDTO.RegisterMembershipResultDTO result =
                userMembershipBrandCommandService.registerMembership(userId, membershipBrandId, request);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    @Operation(
            summary = "멤버십 바코드 조회 기능 API",
            description = "매장 상세 정보 페이지에서 멤버십 바코드를 조회할 수 있습니다."
    )
    @GetMapping("/{membershipBrandId}/barcode")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO>> getUserMembershipBarcodeDTO(
            @PathVariable("membershipBrandId") Long membershipBrandId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        var result = userMembershipBrandQueryService.getUserMembershipBarcode(userId,membershipBrandId);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }

    @Operation(
            summary = "대표 멤버십 설정/해제",
            description = "사용자가 등록한 멤버십을 대표 멤버십으로 설정하거나 해제합니다."
    )
    @PatchMapping("/{userMembershipBrandId}/main")
    public ResponseEntity<ApiResponse<Void>> updateMainMembership(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userMembershipBrandId
    ) {
        Long userId = userDetails.getUserId();

        //  토글 결과 받기
        boolean isNowMain =
                userMembershipBrandCommandService.updateMainMembership(userId, userMembershipBrandId);

        //  상태에 따른 SuccessCode 분기
        MembershipSuccessCode successCode =
                isNowMain
                        ? MembershipSuccessCode.MEMBERSHIP2007
                        : MembershipSuccessCode.MEMBERSHIP2008;

        return ResponseEntity
                .status(successCode.getStatus())
                .body(ApiResponse.onSuccess(successCode, null));
    }

    @Operation(
            summary = "멤버십 기준 적립/할인 가능 매장 조회",
            description = "로그인한 사용자가 보유한 특정 멤버십으로 적립/할인 가능한 매장(브랜드) 목록을 조회합니다. " +
                    "검색어(keyword)를 통해 브랜드명 검색이 가능하며, cursor 기반 페이징을 지원합니다."
    )
    @GetMapping("/{userMembershipBrandId}/stores")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.AvailableStoreListDTO>> getAvailableStores(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userMembershipBrandId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size
    ) {
        if (userDetails == null) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        Long userId = userDetails.getUserId();

        UserMembershipBrandResponseDTO.AvailableStoreListDTO result =
                userMembershipBrandQueryService.getAvailableStores(
                        userId,
                        userMembershipBrandId,
                        keyword,
                        cursor,
                        size
                );

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }
  
    @Operation(
            summary = "사용자 보유 특정 멤버십 상세 정보 조회",
            description = "사용자가 등록한 특정 멤버십의 상세 정보와 적립/할인 가능한 매장 목록을 조회합니다."
    )
    @GetMapping("/{userMembershipBrandId}/detail")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.MembershipDetailDTO>> getMembershipDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userMembershipBrandId
    ) {
        // JWT 인증 체크
        if (userDetails == null) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        Long userId = userDetails.getUserId();

        UserMembershipBrandResponseDTO.MembershipDetailDTO result =
                userMembershipBrandQueryService.getMembershipDetail(userId, userMembershipBrandId);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }
}
