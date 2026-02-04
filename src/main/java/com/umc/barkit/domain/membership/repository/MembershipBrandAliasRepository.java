package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.MembershipBrandAlias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipBrandAliasRepository extends JpaRepository<MembershipBrandAlias, Long> {

    @Query(
            "select a.membershipBrand " +
                    "from MembershipBrandAlias a " +
                    "where a.normalizedAlias = :nq"
        )
    Optional<MembershipBrand> findMembershipByNormalizedAlias(String nq);

    @Query(
            "select a.membershipBrand " +
                    "from MembershipBrandAlias a " +
                    "where :nq like concat('%', a.normalizedAlias, '%') " +
                    "order by length(a.normalizedAlias) desc"
      )
    List<MembershipBrand> findMembershipsContainedInQuery(String nq);

    boolean existsByMembershipBrandIdAndNormalizedAlias(Long membershipBrandId, String normalizedAlias);
}
