package com.umc.barkit.domain.membership.repository;

import com.umc.barkit.domain.membership.entity.MembershipBrand;

import java.util.List;

public interface MembershipBrandRepositoryCustom {

    // 기본 10개 멤버십 브랜드 조회 (QueryDSL)
    List<MembershipBrand> findTop10ByRegistrationCountForDefault();

    // 인기 멤버십 브랜드 10개 조회 (QueryDSL)
    List<MembershipBrand> findTop10ByRegistrationCount();

    // 키워드로 멤버십 브랜드 검색 (커서 페이지네이션)
    List<MembershipBrand> searchByKeyword(String keyword, Long cursor, Integer limit);
}