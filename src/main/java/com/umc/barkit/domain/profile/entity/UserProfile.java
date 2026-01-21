package com.umc.barkit.domain.profile.entity;

import com.umc.barkit.domain.member.entity.User;
import com.umc.barkit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_profile",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User 와 1:1 관계
     * - user가 주 엔티티
     * - profile은 확장 정보
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "bio", length = 255)
    private String bio;
}
