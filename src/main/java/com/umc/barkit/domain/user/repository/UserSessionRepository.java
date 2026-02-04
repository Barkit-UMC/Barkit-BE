package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserSession;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findByUserAndRefreshTokenHashAndRevokedAtIsNull(User user, String refreshTokenHash);

}
