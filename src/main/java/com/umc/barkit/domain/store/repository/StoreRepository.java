package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    boolean existsByKakaoId(String kakaoId);

    Optional<Store> findByKakaoId(String kakaoId);

    Optional<Store> findByGoogleId(String googleId);
}
