package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMembershipBrandRepository extends JpaRepository<UserMembershipBrand, Long>, UserMembershipBrandRepositoryCustom {

    Optional<UserMembershipBrand> findByUserIdAndMembershipBrandId(Long userId,Long membershipBrandId);

}