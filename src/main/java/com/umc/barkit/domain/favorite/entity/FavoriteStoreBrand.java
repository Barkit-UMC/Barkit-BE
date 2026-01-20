package com.umc.barkit.domain.favorite.entity;

import com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus;
import com.umc.barkit.domain.member.entity.User;
import com.umc.barkit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "favorite_store_brand",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "store_brand_id"})
        }
)
public class FavoriteStoreBrand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // store는 다른 분이 구현
    @Column(name = "store_brand_id", nullable = false)
    private Long storeBrandId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FavoriteStoreStatus status = FavoriteStoreStatus.ACTIVE;
}
