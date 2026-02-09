package com.umc.barkit.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
/**
 * (테스트 전용) OAuth "인가 코드(code)" 수신 컨트롤러
 * - OAuth 서버가 redirectUri로 붙여주는 code/state를 그대로 화면에 출력해주고,
 * - 개발자가 그 값을 복사해서 Swagger/Postman으로
 *   POST /api/users/me/oauth/{provider}/connect 에 넣어 테스트할 수 있게 해줌.
 *
 * ⚠ 주의
 * - 이 엔드포인트는 "code를 소비(토큰 교환)"하지 않음.
 * - 오직 code/state를 보여주는 용도이며, 운영 환경에서는 노출하지 않거나 제거할 예정.
 */
public class OAuthCodeReceiverController implements OAuthCodeReceiverControllerDocs{

    // 카카오: redirectUri로 돌아오면 code만 받음
    @GetMapping("/oauth/kakao/code")
    public String kakao(@RequestParam String code) {
        return "code=" + code;
    }

    // 네이버: redirectUri로 돌아오면 code + state를 받음
    @GetMapping("/oauth/naver/code")
    public String naver(@RequestParam String code, @RequestParam String state) {
        return "code=" + code + "\nstate=" + state;
    }
}
