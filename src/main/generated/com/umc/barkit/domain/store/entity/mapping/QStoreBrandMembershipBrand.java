package com.umc.barkit.domain.store.entity.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreBrandMembershipBrand is a Querydsl query type for StoreBrandMembershipBrand
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreBrandMembershipBrand extends EntityPathBase<StoreBrandMembershipBrand> {

    private static final long serialVersionUID = 1111702748L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreBrandMembershipBrand storeBrandMembershipBrand = new QStoreBrandMembershipBrand("storeBrandMembershipBrand");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isMain = createBoolean("isMain");

    public final com.umc.barkit.domain.membership.entity.QMembershipBrand membershipBrand;

    public final com.umc.barkit.domain.store.entity.QStoreBrand storeBrand;

    public QStoreBrandMembershipBrand(String variable) {
        this(StoreBrandMembershipBrand.class, forVariable(variable), INITS);
    }

    public QStoreBrandMembershipBrand(Path<? extends StoreBrandMembershipBrand> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreBrandMembershipBrand(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreBrandMembershipBrand(PathMetadata metadata, PathInits inits) {
        this(StoreBrandMembershipBrand.class, metadata, inits);
    }

    public QStoreBrandMembershipBrand(Class<? extends StoreBrandMembershipBrand> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.membershipBrand = inits.isInitialized("membershipBrand") ? new com.umc.barkit.domain.membership.entity.QMembershipBrand(forProperty("membershipBrand")) : null;
        this.storeBrand = inits.isInitialized("storeBrand") ? new com.umc.barkit.domain.store.entity.QStoreBrand(forProperty("storeBrand")) : null;
    }

}

