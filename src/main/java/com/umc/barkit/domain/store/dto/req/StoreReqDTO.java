package com.umc.barkit.domain.store.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;


public class StoreReqDTO {

    public record SearchReq(
            @NotBlank(message = "query는 필수입니다.")
            @Size(max = 20, message = "query는 최대 20자까지 가능합니다.")
            @Pattern(
                    regexp = "^[가-힣a-zA-Z0-9 ]+$",
                    message = "query는 한글/영문/숫자(공백)만 입력 가능합니다."
            )
            String query,

            @NotNull(message = "userLat는 필수입니다.")
            @DecimalMin(value = "33.0", message = "userLat는 한국 범위를 벗어났습니다.")
            @DecimalMax(value = "39.5", message = "userLat는 한국 범위를 벗어났습니다.")
            Double userLat,

            @NotNull(message = "userLng는 필수입니다.")
            @DecimalMin(value = "124.0", message = "userLng는 한국 범위를 벗어났습니다.")
            @DecimalMax(value = "132.0", message = "userLng는 한국 범위를 벗어났습니다.")
            Double userLng,

            @DecimalMin(value = "33.0", message = "centerLat는 한국 범위를 벗어났습니다.")
            @DecimalMax(value = "39.5", message = "centerLat는 한국 범위를 벗어났습니다.")
            Double centerLat,

            @DecimalMin(value = "124.0", message = "centerLng는 한국 범위를 벗어났습니다.")
            @DecimalMax(value = "132.0", message = "centerLng는 한국 범위를 벗어났습니다.")
            Double centerLng
    ) {
        //centerLat, centerLng는 같이 와야 함
        @AssertTrue(message = "centerLat와 centerLng는 함께 전달되어야 합니다.")
        @Schema(hidden = true)
        public boolean isCenterPairValid() {
            return (centerLat == null && centerLng == null) || (centerLat != null && centerLng != null);
        }
    }
}
