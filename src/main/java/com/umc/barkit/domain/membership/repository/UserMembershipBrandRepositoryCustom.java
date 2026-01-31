package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.UserMembershipBrand;

import java.util.List;

public interface UserMembershipBrandRepositoryCustom {

    // 사용자 보유 멤버십 브랜드 검색 (커서 페이지네이션)
    List<UserMembershipBrand> searchByUserIdAndKeyword(
            Long userId,
            String keyword,
            Long cursor,
            Integer limit
    );
}