package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.mapping.StoreBrandMembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBrandMembershipBrandRepository extends JpaRepository<StoreBrandMembershipBrand, Long> {
    @Query("select sbmb.storeBrand.id " +
            "from StoreBrandMembershipBrand sbmb "+
            "where sbmb.membershipBrand.id = :membershipBrandId"
    )
    List<Long> findStoreBrandIdsByMembershipBrandId(Long membershipBrandId);

    @Query("select sbmb.membershipBrand.id " +
            "from StoreBrandMembershipBrand sbmb "+
            "where sbmb.storeBrand.id = :storeBrandId"
    )
    List<Long> findMembershipBrandIdsByStoreBrandId(Long storeBrandId);

    List<StoreBrandMembershipBrand> findByStoreBrandId(Long storeBrandId);
}
