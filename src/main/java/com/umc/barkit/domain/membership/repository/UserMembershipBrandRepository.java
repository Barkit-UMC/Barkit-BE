package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMembershipBrandRepository
        extends JpaRepository<UserMembershipBrand, Long>,
        UserMembershipBrandRepositoryCustom {

    /**
     * 사용자 보유 멤버십 목록 조회
     * - 대표 멤버십 정렬은 서비스 레이어에서 처리
     */
    List<UserMembershipBrand> findByUserId(Long userId);
}