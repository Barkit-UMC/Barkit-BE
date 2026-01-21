package com.umc.barkit.domain.member.entity;

import com.umc.barkit.domain.member.enums.AuthProvider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "user_oauth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_oauth_provider_uid", columnNames = {"provider", "provider_uid"})
        })
public class UserOauth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider;

    @Column(name = "provider_uid", length = 128, nullable = false)
    private String providerUid;

    @Column(name = "provider_email", length = 320)
    private String providerEmail;

    @Column(name = "connected_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime connectedAt;

    @Column(name = "disconnected_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime disconnectedAt;

    public boolean isDisconnected() {
        return disconnectedAt != null;
    }

    public void disconnect() {
        this.disconnectedAt = LocalDateTime.now();
    }
}
