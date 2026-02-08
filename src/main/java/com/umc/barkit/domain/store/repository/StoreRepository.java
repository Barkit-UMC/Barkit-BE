package com.umc.barkit.domain.store.repository;

import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.repository.projection.BrandViewCountProjection;
import com.umc.barkit.domain.store.repository.projection.ViewCountProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
    Optional<Store> findByGoogleId(String googleId);

    List<Store> findAllByGoogleIdIn(Collection<String> googleIds);

    @Query("select s.googleId as googleId, s.viewCount as viewCount " +
            "from Store s " +
            "where s.googleId in :googleIds"
        )
    List<ViewCountProjection> findViewCountsByGoogleIds(@Param("googleIds") Collection<String> googleIds);

    //  멤버십 기준 매장 조회 (검색 optional)
    @Query("""
    select s.id
    from Store s
    where s.brand.id = :brandId
    order by s.id asc
""")
    List<Long> findStoreIdsByBrandId(
            @Param("brandId") Long brandId,
            Pageable pageable
    );

    // 멤버십 기준 인기 매장 조회
    @Query("""
    select
        sb.id as storeBrandId,
        sb.name as storeBrandName,
        sum(s.viewCount) as totalViewCount
    from Store s
    join s.brand sb
    where sb.id in :brandIds
    group by sb.id, sb.name
    order by sum(s.viewCount) desc
""")
    List<BrandViewCountProjection> findPopularBrandsByBrandIds(
            @Param("brandIds") List<Long> brandIds,
            Pageable pageable
    );
}
