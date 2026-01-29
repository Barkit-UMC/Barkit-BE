package com.umc.barkit.domain.favorite.dto.response;

import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FavoriteResponse {

    private Long favoriteId;
    private Long storeBrandId;
    private String storeBrandName;

    public static FavoriteResponse from(FavoriteStoreBrand favorite) {
        return FavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .storeBrandId(favorite.getStoreBrandId())
                .storeBrandName(
                        favorite.getStoreBrand() != null
                                ? favorite.getStoreBrand().getName()
                                : ""
                )
                .build();
    }
}
