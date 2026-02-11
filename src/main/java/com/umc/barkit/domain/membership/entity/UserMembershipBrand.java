package com.umc.barkit.domain.membership.entity;

import com.umc.barkit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_membership_brand")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserMembershipBrand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "membership_brand_id", nullable = false)
    private Long membershipBrandId;

    @Column(name = "membership_number", nullable = false, length = 30)
    private String membershipNumber;

    @Builder.Default
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    // 도메인 로직 메서드
    public void updateIsMain(boolean isMain) {
        this.isMain = isMain;
    }

    public void updateMembershipNumber(String membershipNumber) {
        this.membershipNumber = membershipNumber;
    }
}