package com.umc.barkit.domain.favorite.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FavoriteCreateRequest {

    @NotNull(message = "storeBrandId는 필수입니다.")
    private Long storeBrandId;
}
