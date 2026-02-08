package com.umc.barkit.domain.home.dto.response;

import com.umc.barkit.domain.store.repository.projection.BrandViewCountProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HomePopularStoreResponse {

    /**
     * 인기 브랜드 목록 (최대 5개)
     */
    private List<PopularBrand> brands;

    public static HomePopularStoreResponse fromBrands(
            List<BrandViewCountProjection> brands
    ) {
        return new HomePopularStoreResponse(
                brands.stream()
                        .map(PopularBrand::from)
                        .toList()
        );
    }

    @Getter
    @AllArgsConstructor
    public static class PopularBrand {

        /**
         * 매장 브랜드 ID
         */
        private Long storeBrandId;

        /**
         * 매장 브랜드 이름
         */
        private String storeBrandName;

        /**
         * 브랜드 전체 조회수 합계 (선택)
         * → 프론트에서 필요 없으면 빼도 됨
         */
        private Long totalViewCount;

        public static PopularBrand from(BrandViewCountProjection projection) {
            return new PopularBrand(
                    projection.getStoreBrandId(),
                    projection.getStoreBrandName(),
                    projection.getTotalViewCount()
            );
        }
    }
}
