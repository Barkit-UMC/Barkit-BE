package com.umc.barkit.domain.membership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QMembershipBrand is a Querydsl query type for MembershipBrand
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMembershipBrand extends EntityPathBase<MembershipBrand> {

    private static final long serialVersionUID = 275671087L;

    public static final QMembershipBrand membershipBrand = new QMembershipBrand("membershipBrand");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    public final StringPath color = createString("color");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath logoUrl = createString("logoUrl");

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QMembershipBrand(String variable) {
        super(MembershipBrand.class, forVariable(variable));
    }

    public QMembershipBrand(Path<? extends MembershipBrand> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMembershipBrand(PathMetadata metadata) {
        super(MembershipBrand.class, metadata);
    }

}

