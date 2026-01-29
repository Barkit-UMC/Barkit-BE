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

    @Column(name = "membership_number", length = 30)
    private String membershipNumber;

    @Column(name = "barcode_raw_value", nullable = false, length = 50)
    private String barcodeRawValue;

}