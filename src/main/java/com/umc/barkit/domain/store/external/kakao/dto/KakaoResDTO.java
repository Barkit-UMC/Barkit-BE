package com.umc.barkit.domain.store.external.kakao.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class KakaoResDTO {
    private List<Document> documents;

    @Builder
    public record Document(
            String id,
            String place_name,
            String address_name,
            String road_address_name,
            String phone,
            String x,
            String y
    ) {}
}

