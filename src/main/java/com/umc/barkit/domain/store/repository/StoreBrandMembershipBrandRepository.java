package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.mapping.StoreBrandMembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreBrandMembershipBrandRepository extends JpaRepository<StoreBrandMembershipBrand,Long> {
}
