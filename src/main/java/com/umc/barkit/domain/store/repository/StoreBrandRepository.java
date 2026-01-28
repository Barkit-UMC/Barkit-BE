package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.Facility;
import com.umc.barkit.domain.store.entity.StoreBrand;
import com.umc.barkit.domain.store.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreBrandRepository extends JpaRepository<StoreBrand,Long> {
    //전체 조회
    @Query(
            "select sb.name " +
                    "from StoreBrand sb " +
                    "where sb.id = :storeBrandId"
    )
    String findNameById(Long storeBrandId);

    //카테고리 조회
    @Query(
            "select sb.name " +
                    "from StoreBrand sb " +
                    "where sb.id = :storeBrandId and sb.category = :category"
    )
    Optional<String> findNameByIdAndCategory(Long storeBrandId, Category category);
}
