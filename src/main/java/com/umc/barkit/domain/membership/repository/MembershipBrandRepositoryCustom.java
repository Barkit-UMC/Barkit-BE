package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.MembershipBrand;

import java.util.List;

public interface MembershipBrandRepositoryCustom {

    // 상위 4개 인기 멤버십 브랜드 조회 (QueryDSL)
    List<MembershipBrand> findTop4ByRegistrationCount();

    // 인기 멤버십 브랜드 10개 조회 (QueryDSL)
    List<MembershipBrand> findTop10ByRegistrationCount();
}