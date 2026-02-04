package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.entity.StoreBrandAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreBrandAliasRepository extends JpaRepository<StoreBrandAlias, Long> {


    @Query(
            "select a.storeBrand " +
                    "from StoreBrandAlias a " +
                    "where a.normalizedAlias = :nq"
        )
    Optional<StoreBrand> findBrandByNormalizedAlias(String nq);


    @Query(
            "select a.storeBrand " +
                    "from StoreBrandAlias a " +
                    "where :nq like concat('%', a.normalizedAlias, '%') " +
                    "order by length(a.normalizedAlias) desc"
        )
    List<StoreBrand> findBrandsContainedInQuery(String nq);

    boolean existsByStoreBrandIdAndNormalizedAlias(Long storeBrandId, String normalizedAlias);

}
