package com.umc.barkit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "refresh_token_hash", length = 255, nullable = false)
    private String refreshTokenHash;

    @Column(name = "remember_me", nullable = false)
    private Boolean rememberMe;

    @Column(name = "issued_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "expires_at", columnDefinition = "DATETIME(6)", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "last_used_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime lastUsedAt;

    @Column(name = "revoked_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime revokedAt; // 무효화 시각

    public boolean isRevoked() {
        return revokedAt != null;
    }

    // 세션 무효화 처리
    public void revoke() {
        this.revokedAt = LocalDateTime.now();
    }

    // refresh token 마지막 사용 시각 업데이트
    public void touch() {
        this.lastUsedAt = LocalDateTime.now();
    }
}
