package com.umc.barkit.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserOauth is a Querydsl query type for UserOauth
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserOauth extends EntityPathBase<UserOauth> {

    private static final long serialVersionUID = 1082422357L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserOauth userOauth = new QUserOauth("userOauth");

    public final DateTimePath<java.time.LocalDateTime> connectedAt = createDateTime("connectedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> disconnectedAt = createDateTime("disconnectedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.umc.barkit.domain.user.enums.AuthProvider> provider = createEnum("provider", com.umc.barkit.domain.user.enums.AuthProvider.class);

    public final StringPath providerEmail = createString("providerEmail");

    public final StringPath providerUid = createString("providerUid");

    public final QUser user;

    public QUserOauth(String variable) {
        this(UserOauth.class, forVariable(variable), INITS);
    }

    public QUserOauth(Path<? extends UserOauth> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserOauth(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserOauth(PathMetadata metadata, PathInits inits) {
        this(UserOauth.class, metadata, inits);
    }

    public QUserOauth(Class<? extends UserOauth> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

