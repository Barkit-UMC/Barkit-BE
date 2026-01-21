package com.umc.barkit.domain.favorite.repository;

import com.umc.barkit.domain.favorite.entity.FavoriteStoreBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteStoreBrandRepository
        extends JpaRepository<FavoriteStoreBrand, Long> {
}
