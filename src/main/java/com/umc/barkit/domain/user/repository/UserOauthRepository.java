package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.UserOauth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {
}
