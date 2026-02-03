package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipBrandRepository
        extends JpaRepository<UserMembershipBrand, Long>,
        UserMembershipBrandRepositoryCustom {

    // 홈 대시보드 / 멤버십 목록 조회
    List<UserMembershipBrand> findByUserId(Long userId);

    // 단건 조회 (기본 JPA 기능이지만 명시적으로 쓰는 경우)
    Optional<UserMembershipBrand> findById(Long id);
}
