package com.umc.barkit.domain.membership.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.QMembershipBrand;
import com.umc.barkit.domain.membership.entity.QUserMembershipBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class MembershipBrandRepositoryImpl implements MembershipBrandRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QMembershipBrand membershipBrand = QMembershipBrand.membershipBrand;
    private final QUserMembershipBrand userMembershipBrand = QUserMembershipBrand.userMembershipBrand;

    // 영문 → 한글 매핑
    private static final Map<String, String> ENGLISH_TO_KOREAN = new HashMap<>() {{
        put("naver", "네이버");
        put("kakao", "카카오");
        put("happy", "해피");
        put("point", "포인트");
    }};

    @Override
    public List<MembershipBrand> findTop10ByRegistrationCountForDefault() {
        return queryFactory
                .selectFrom(membershipBrand)
                .leftJoin(userMembershipBrand)
                .on(userMembershipBrand.membershipBrandId.eq(membershipBrand.id))
                .groupBy(membershipBrand.id)
                .orderBy(
                        userMembershipBrand.count().desc(),
                        membershipBrand.id.asc()
                )
                .limit(10)
                .fetch();
    }

    @Override
    public List<MembershipBrand> findTop10ByRegistrationCount() {
        return queryFactory
                .selectFrom(membershipBrand)
                .leftJoin(userMembershipBrand)
                .on(userMembershipBrand.membershipBrandId.eq(membershipBrand.id))
                .groupBy(membershipBrand.id)
                .orderBy(
                        userMembershipBrand.count().desc(),
                        membershipBrand.id.asc()
                )
                .limit(10)
                .fetch();
    }

    @Override
    public List<MembershipBrand> searchByKeyword(String keyword, Long cursor, Integer limit) {
        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 1. 검색어 정규화
            String normalizedKeyword = keyword.replaceAll("[\\s.+-]", "").toLowerCase();

            // 2. 영문 → 한글 변환 (네이버, 카카오만)
            String mappedKeyword = ENGLISH_TO_KOREAN.getOrDefault(normalizedKeyword, normalizedKeyword);

            // 3. 검색 (원본 OR 매핑된 키워드)
            builder.and(
                    Expressions.stringTemplate(
                                    "REPLACE(REPLACE(REPLACE(REPLACE(LOWER({0}), ' ', ''), '.', ''), '+', ''), '-', '')",
                                    membershipBrand.name
                            ).contains(normalizedKeyword)
                            .or(
                                    membershipBrand.name.containsIgnoreCase(mappedKeyword)
                            )
            );
        }

        if (cursor != null) {
            builder.and(membershipBrand.id.gt(cursor));
        }

        return queryFactory
                .selectFrom(membershipBrand)
                .where(builder)
                .orderBy(membershipBrand.id.asc())
                .limit(limit + 1)
                .fetch();
    }
}