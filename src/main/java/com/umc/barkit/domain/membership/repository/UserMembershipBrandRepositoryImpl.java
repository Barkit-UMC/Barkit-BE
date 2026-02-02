package com.umc.barkit.domain.membership.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umc.barkit.domain.membership.entity.QMembershipBrand.membershipBrand;
import static com.umc.barkit.domain.membership.entity.QUserMembershipBrand.userMembershipBrand;

@RequiredArgsConstructor
public class UserMembershipBrandRepositoryImpl implements UserMembershipBrandRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final Map<String, String> ENGLISH_TO_KOREAN = new HashMap<>() {{
        put("naver", "네이버");
        put("kakao", "카카오");
        put("happy", "해피");
        put("point", "포인트");
    }};

    @Override
    public List<UserMembershipBrand> searchByUserIdAndKeyword(
            Long userId,
            String keyword,
            Long cursor,
            Integer limit
    ) {
        BooleanBuilder builder = new BooleanBuilder();

        // 1. 사용자 ID 필터 (필수)
        builder.and(userMembershipBrand.userId.eq(userId));

        // 2. 키워드 검색 (MembershipBrand name 기준)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String normalizedKeyword = keyword.replaceAll("[\\s.+-]", "").toLowerCase();

            String mappedKeyword = ENGLISH_TO_KOREAN.getOrDefault(normalizedKeyword, normalizedKeyword);

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

        // 3. 커서 조건
        if (cursor != null) {
            builder.and(userMembershipBrand.id.gt(cursor));
        }

        // 4. 쿼리 실행 (JOIN 필수!)
        return queryFactory
                .selectFrom(userMembershipBrand)
                .join(membershipBrand)
                .on(userMembershipBrand.membershipBrandId.eq(membershipBrand.id))
                .where(builder)
                .orderBy(userMembershipBrand.id.asc())
                .limit(limit + 1)
                .fetch();
    }

    @Override
    public boolean existsByUserIdAndMembershipBrandId(Long userId, Long membershipBrandId) {
        Long count = queryFactory
                .select(userMembershipBrand.count())
                .from(userMembershipBrand)
                .where(
                        userMembershipBrand.userId.eq(userId),
                        userMembershipBrand.membershipBrandId.eq(membershipBrandId)
                )
                .fetchOne();

        return count != null && count > 0;
    }
}