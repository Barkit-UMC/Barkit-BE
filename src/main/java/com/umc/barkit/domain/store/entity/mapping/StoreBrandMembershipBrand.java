package com.umc.barkit.domain.store.entity.mapping;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.store.entity.StoreBrand;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class StoreBrandMembershipBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_brand_id", nullable = false)
    private MembershipBrand membershipBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_brand_id", nullable = false)
    private StoreBrand storeBrand;

    @Column(name = "popularity_rank")
    private Long popularityRank;
}
