package com.umc.barkit.domain.user.entity;

import com.umc.barkit.domain.user.enums.Role;
import com.umc.barkit.domain.user.enums.UserStatus;
import com.umc.barkit.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Column(name = "email", length = 320, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status;

    @Column(name = "deleted_at", columnDefinition = "DATETIME(6)")
    private LocalDateTime deletedAt;

    @Builder.Default
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled = true;

    @Builder.Default
    @Column(name = "location_consent", nullable = false)
    private Boolean locationConsent = false;

    public void softDelete(String anonymizedEmail, LocalDateTime now) {
        this.deletedAt = now;
        this.status = UserStatus.INACTIVE;
        this.email = anonymizedEmail;
    }

    // 생년월일 변경
    public void updateBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // 비밀번호 변경
    public void updatePassword(String encodedPassword) {
        this.passwordHash = encodedPassword;
    }

    // 알림 수신 여부 변경
    public void updateNotificationEnabled(Boolean enabled) { this.notificationEnabled = enabled; }

    // 알림 수신 여부 변경
    public void updateLocationConsent(Boolean consented) { this.locationConsent = consented; }
}
