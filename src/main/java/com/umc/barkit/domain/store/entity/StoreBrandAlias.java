package com.umc.barkit.domain.store.entity;

import com.umc.barkit.domain.store.util.StoreUtil;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name="store_brand_alias")
public class StoreBrandAlias {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alias", length = 20, unique = true)
    private String alias;

    @Column(name = "normalized_alias", nullable = false, length = 20)
    private String normalizedAlias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_brand_id", nullable = false)
    private StoreBrand storeBrand;

    @PrePersist
    @PreUpdate
    private void syncNormalizedAlias() {
        this.normalizedAlias = StoreUtil.searchNormalize(this.alias);
    }
}
