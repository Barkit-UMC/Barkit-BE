package com.umc.barkit.domain.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserSession is a Querydsl query type for UserSession
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserSession extends EntityPathBase<UserSession> {

    private static final long serialVersionUID = 193491156L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserSession userSession = new QUserSession("userSession");

    public final DateTimePath<java.time.LocalDateTime> expiresAt = createDateTime("expiresAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> issuedAt = createDateTime("issuedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> lastUsedAt = createDateTime("lastUsedAt", java.time.LocalDateTime.class);

    public final StringPath refreshTokenHash = createString("refreshTokenHash");

    public final BooleanPath rememberMe = createBoolean("rememberMe");

    public final DateTimePath<java.time.LocalDateTime> revokedAt = createDateTime("revokedAt", java.time.LocalDateTime.class);

    public final QUser user;

    public QUserSession(String variable) {
        this(UserSession.class, forVariable(variable), INITS);
    }

    public QUserSession(Path<? extends UserSession> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUserSession(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUserSession(PathMetadata metadata, PathInits inits) {
        this(UserSession.class, metadata, inits);
    }

    public QUserSession(Class<? extends UserSession> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user")) : null;
    }

}

