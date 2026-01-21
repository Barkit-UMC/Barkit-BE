package com.umc.barkit.domain.membership.entity;

import com.umc.barkit.global.entity.BaseEntity;
import com.umc.barkit.domain.membership.enums.Color;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "membership_brand")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MembershipBrand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false, length = 20)
    private Color color;

}