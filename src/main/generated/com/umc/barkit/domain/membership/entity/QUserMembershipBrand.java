package com.umc.barkit.domain.membership.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserMembershipBrand is a Querydsl query type for UserMembershipBrand
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserMembershipBrand extends EntityPathBase<UserMembershipBrand> {

    private static final long serialVersionUID = -1931794972L;

    public static final QUserMembershipBrand userMembershipBrand = new QUserMembershipBrand("userMembershipBrand");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    public final StringPath barcodeImageUrl = createString("barcodeImageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> membershipBrandId = createNumber("membershipBrandId", Long.class);

    public final StringPath membershipNumber = createString("membershipNumber");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserMembershipBrand(String variable) {
        super(UserMembershipBrand.class, forVariable(variable));
    }

    public QUserMembershipBrand(Path<? extends UserMembershipBrand> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserMembershipBrand(PathMetadata metadata) {
        super(UserMembershipBrand.class, metadata);
    }

}

