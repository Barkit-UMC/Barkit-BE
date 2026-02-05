package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.store.entity.StoreBrand;

import java.util.List;

public interface UserMembershipBrandRepositoryCustom {

    // 사용자 보유 멤버십 브랜드 검색 (커서 페이지네이션)
    List<UserMembershipBrand> searchByUserIdAndKeyword(
            Long userId,
            String keyword,
            Long cursor,
            Integer limit
    );

    // 멤버십 브랜드 중복 체크
    boolean existsByUserIdAndMembershipBrandId(Long userId, Long membershipBrandId);

    // 대표 멤버십 개수 조회
    int countMainByUserId(Long userId);

    // 멤버십 브랜드에 연관된 StoreBrand 목록 조회
    List<StoreBrand> findStoreBrandsByMembershipBrandId(Long membershipBrandId);
}