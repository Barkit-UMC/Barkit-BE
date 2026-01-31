package com.umc.barkit.domain.membership.controller;

import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.service.UserMembershipBrandQueryService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-membership-brands")
public class UserMembershipBrandController {

    private final UserMembershipBrandQueryService userMembershipBrandQueryService;

    @Operation(
            summary = "사용자 보유 멤버십 브랜드 검색",
            description = "로그인한 사용자가 보유한 멤버십 브랜드를 키워드로 검색합니다. " +
                    "대소문자 무시, 띄어쓰기/특수문자 무시, 커서 기반 페이지네이션을 지원합니다."
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<UserMembershipBrandResponseDTO.SearchResultDTO>> searchUserMembershipBrands(
            @Parameter(description = "사용자 ID", required = true, example = "100")
            @RequestParam Long userId,  // TODO: 추후 JWT에서 추출

            @Parameter(description = "검색 키워드", required = true)
            @RequestParam String keyword,

            @Parameter(description = "커서", required = false, example = "0")
            @RequestParam(required = false) Long cursor,

            @Parameter(description = "한 번에 가져올 개수 (기본값: 20)", required = false, example = "20")
            @RequestParam(required = false) Integer limit
    ) {
        UserMembershipBrandResponseDTO.SearchResultDTO result =
                userMembershipBrandQueryService.searchUserMembershipBrands(userId, keyword, cursor, limit);

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }
}