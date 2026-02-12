package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.mapping.StoreBrandMembershipBrand;
import com.umc.barkit.domain.store.repository.projection.BrandMembershipProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


    //여러 브랜드의 멤버십 정보를 한 번에 가져오기
    @Query("""
        select
            sbmb.storeBrand.id as storeBrandId,
            mb.id as membershipId,
            mb.name as membershipName,
            mb.logoUrl as membershipLogoUrl
        from StoreBrandMembershipBrand sbmb
        join sbmb.membershipBrand mb
        where sbmb.storeBrand.id in :storeBrandIds
    """)
    List<BrandMembershipProjection> findMembershipsByStoreBrandIds(
            List<Long> storeBrandIds
    );
}
