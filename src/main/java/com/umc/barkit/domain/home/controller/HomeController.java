package com.umc.barkit.domain.home.controller;

import com.umc.barkit.domain.home.dto.response.HomeDashboardResponse;
import com.umc.barkit.domain.home.service.HomeDashboardService;
import com.umc.barkit.global.apiPayload.ApiResponse;
import com.umc.barkit.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private final HomeDashboardService homeDashboardService;

    @Operation(
            summary = "홈 대시보드 조회",
            description = "홈 화면 진입 시 필요한 핵심 정보를 한 번에 조회합니다. " +
                    "즐겨찾기 매장 요약, 보유 멤버십 카드 정보를 포함합니다."
    )
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<HomeDashboardResponse.DashboardDTO>> getHomeDashboard() {

        HomeDashboardResponse.DashboardDTO result =
                homeDashboardService.getDashboard();

        return ResponseEntity
                .status(GeneralSuccessCode.OK.getStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.OK, result));
    }
}
