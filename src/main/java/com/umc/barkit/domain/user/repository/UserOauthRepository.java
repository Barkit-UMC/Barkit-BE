package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.UserOauth;
import com.umc.barkit.domain.user.enums.AuthProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {

    Optional<UserOauth> findByProviderAndProviderUidAndDisconnectedAtIsNull(AuthProvider provider, String providerUid);
    List<UserOauth> findAllByUser_IdAndDisconnectedAtIsNull(Long userId);
}
