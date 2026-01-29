package com.umc.barkit.domain.favorite.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFavoriteStoreBrand is a Querydsl query type for FavoriteStoreBrand
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoriteStoreBrand extends EntityPathBase<FavoriteStoreBrand> {

    private static final long serialVersionUID = -1020761014L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFavoriteStoreBrand favoriteStoreBrand = new QFavoriteStoreBrand("favoriteStoreBrand");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> mainUserMembershipBrandId = createNumber("mainUserMembershipBrandId", Long.class);

    public final EnumPath<com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus> status = createEnum("status", com.umc.barkit.domain.favorite.enums.FavoriteStoreStatus.class);

    public final com.umc.barkit.domain.store.entity.QStoreBrand storeBrand;

    public final NumberPath<Long> storeBrandId = createNumber("storeBrandId", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.umc.barkit.domain.user.entity.QUser user;

    public QFavoriteStoreBrand(String variable) {
        this(FavoriteStoreBrand.class, forVariable(variable), INITS);
    }

    public QFavoriteStoreBrand(Path<? extends FavoriteStoreBrand> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFavoriteStoreBrand(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFavoriteStoreBrand(PathMetadata metadata, PathInits inits) {
        this(FavoriteStoreBrand.class, metadata, inits);
    }

    public QFavoriteStoreBrand(Class<? extends FavoriteStoreBrand> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.storeBrand = inits.isInitialized("storeBrand") ? new com.umc.barkit.domain.store.entity.QStoreBrand(forProperty("storeBrand")) : null;
        this.user = inits.isInitialized("user") ? new com.umc.barkit.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

