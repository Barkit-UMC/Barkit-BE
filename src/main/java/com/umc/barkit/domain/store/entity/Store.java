package com.umc.barkit.domain.store.entity;

import com.umc.barkit.global.entity.BaseEntity;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="store")
public class Store extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "google_id", length = 255, unique = true, nullable = false)
    private String googleId;

    @Column(name = "kakao_id", length = 255, unique = true, nullable = false)
    private String kakaoId;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_brand_id", nullable = false)
    private StoreBrand brand;

    public void addViewCount() {
        this.viewCount++;
    }
}
