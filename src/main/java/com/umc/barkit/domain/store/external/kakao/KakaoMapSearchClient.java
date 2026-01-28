package com.umc.barkit.domain.store.external.kakao;

import com.umc.barkit.domain.store.external.kakao.dto.KakaoResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KakaoMapSearchClient {

    private final WebClient kakaoWebClient;

    public List<KakaoResDTO.Document> searchByKeyword(
            String keyword,
            Double lat,
            Double lng
    ) {
        //카카오
        KakaoResDTO response =
                kakaoWebClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .path("/v2/local/search/keyword.json")
                                .queryParam("query", keyword)
                                .queryParam("y", lat)
                                .queryParam("x", lng)
                                .queryParam("radius", 20000) // 20km
                                .queryParam("size", 15)
                                .build()
                        )
                        .retrieve()
                        .onStatus(
                                HttpStatusCode::isError,
                                clientResponse -> Mono.error(
                                        new RuntimeException("카카오맵 API 호출 실패")) // 추후에 수정 예정
                        )
                        .bodyToMono(KakaoResDTO.class)
                        .block();

        if (response == null || response.getDocuments() == null) {
            return List.of();
        }

        return response.getDocuments();
    }
}

