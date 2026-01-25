package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.MembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipBrandRepository extends JpaRepository<MembershipBrand, Long> {
    boolean existsByName(String name);

    Optional<MembershipBrand> findByName(String name);

}