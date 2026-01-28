package com.umc.barkit.domain.membership.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.QMembershipBrand;
import com.umc.barkit.domain.membership.entity.QUserMembershipBrand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MembershipBrandRepositoryImpl implements MembershipBrandRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QMembershipBrand membershipBrand = QMembershipBrand.membershipBrand;
    private final QUserMembershipBrand userMembershipBrand = QUserMembershipBrand.userMembershipBrand;

    @Override
    public List<MembershipBrand> findTop4ByRegistrationCount() {
        return queryFactory
                .selectFrom(membershipBrand)
                .leftJoin(userMembershipBrand)
                .on(userMembershipBrand.membershipBrandId.eq(membershipBrand.id))
                .groupBy(membershipBrand.id)
                .orderBy(
                        userMembershipBrand.count().desc(),
                        membershipBrand.id.asc()
                )
                .limit(4)
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
}