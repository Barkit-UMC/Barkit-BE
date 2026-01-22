package com.umc.barkit.domain.favorite.entity;

import com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus;
import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.store.entity.StoreBrand;
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

    /* ===== User 연관 ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /* ===== StoreBrand FK (쓰기 책임) ===== */
    @Column(name = "store_brand_id", nullable = false)
    private Long storeBrandId;

    /* ===== StoreBrand 객체 탐색용 (읽기 전용) ===== */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "store_brand_id",
            insertable = false,
            updatable = false
    )
    private StoreBrand storeBrand;

    /* ===== 메인 바코드 설정 ===== */
    @Column(name = "main_user_membership_brand_id")
    private Long mainUserMembershipBrandId;

    /* ===== 상태 ===== */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FavoriteStoreStatus status = FavoriteStoreStatus.ACTIVE;

    /* ===== 도메인 로직 ===== */
    public void delete() {
        this.status = FavoriteStoreStatus.DELETED;
    }
}
