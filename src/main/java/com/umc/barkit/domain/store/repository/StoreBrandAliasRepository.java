package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.entity.StoreBrandAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBrandAliasRepository extends JpaRepository<StoreBrandAlias, Long> {

    // 1) exact match: "olive young" -> "oliveyoung" == normalized_alias
    @Query("""
        select a.storeBrand
        from StoreBrandAlias a
        where a.normalizedAlias = :nq
    """)
    Optional<StoreBrand> findBrandByNormalizedAlias(@Param("nq") String normalizedAlias);

    // 2) contains match: "올리브영성수점" 안에 "올리브영"이 포함되면 매칭
    @Query("""
        select a.storeBrand
        from StoreBrandAlias a
        where :nq like concat('%', a.normalizedAlias, '%')
        order by length(a.normalizedAlias) desc
    """)
    List<StoreBrand> findBrandsContainedInQuery(@Param("nq") String normalizedQuery);

    boolean existsByStoreBrandIdAndNormalizedAlias(Long storeBrandId, String normalizedAlias);
}
