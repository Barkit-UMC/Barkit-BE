package com.umc.barkit.domain.user.repository;

import com.umc.barkit.domain.user.entity.User;
import com.umc.barkit.domain.user.entity.UserOauth;
import com.umc.barkit.domain.user.enums.AuthProvider;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOauthRepository extends JpaRepository<UserOauth, Long> {

    // providerUid가 이미 다른 계정에 연동돼 있나
    Optional<UserOauth> findByProviderAndProviderUidAndDisconnectedAtIsNull(AuthProvider provider, String providerUid);

    List<UserOauth> findAllByUser_IdAndDisconnectedAtIsNull(Long userId);

    // 예전에 끊긴 row가 있으면 reconnect로 재사용
    Optional<UserOauth> findByProviderAndProviderUid(AuthProvider provider, String providerUid);

    // 내 계정이 이미 해당 provider를 연동 중인가
    Optional<UserOauth> findByUserAndProviderAndDisconnectedAtIsNull(User user, AuthProvider provider);
}
