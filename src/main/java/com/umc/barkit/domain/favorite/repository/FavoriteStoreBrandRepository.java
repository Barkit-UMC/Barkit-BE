package com.umc.barkit.domain.favorite.repository;

import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteStoreBrandRepository extends JpaRepository<FavoriteStoreBrand, Long> {

    // 로그인 유저 기준 ACTIVE 즐겨찾기 최신 5개
    List<FavoriteStoreBrand> findTop5ByUser_IdAndStatusOrderByCreatedAtDesc(
            Long userId,
            FavoriteStoreStatus status
    );

    // 중복 즐겨찾기 방지 (ACTIVE 기준)
    boolean existsByUser_IdAndStoreBrandIdAndStatus(
            Long userId,
            Long storeBrandId,
            FavoriteStoreStatus status
    );

    // 삭제/조회 권한 체크
    Optional<FavoriteStoreBrand> findByIdAndUser_Id(Long id, Long userId);

    // (선택) 즐겨찾기 5개 제한 체크용
    long countByUser_IdAndStatus(Long userId, FavoriteStoreStatus status);
}
