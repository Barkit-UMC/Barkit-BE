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

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @Column(name = "address", length = 100, nullable = false)
    private String address;

    @Column(name = "homepage", length = 255)
    private String homepage;

    @Column(name = "latitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal lat;

    @Column(name = "longitude", precision = 10, scale = 7, nullable = false)
    private BigDecimal lng;

    @Column(name = "google_id", length = 255, nullable = false)
    private String googleId;

    @Column(name = "kakao_id", length = 255, nullable = false)
    private String kakaoId;

    @OneToMany(mappedBy = "store")
    private List<Facility> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<StoreHour> hours = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<StorePhoto> photos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private StoreBrand brand;
}
