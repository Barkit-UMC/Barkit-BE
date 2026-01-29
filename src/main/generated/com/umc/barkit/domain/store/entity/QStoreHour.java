package com.umc.barkit.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreHour is a Querydsl query type for StoreHour
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreHour extends EntityPathBase<StoreHour> {

    private static final long serialVersionUID = -1702207978L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreHour storeHour = new QStoreHour("storeHour");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    public final TimePath<java.time.LocalTime> close = createTime("close", java.time.LocalTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final EnumPath<java.time.DayOfWeek> dayOfWeek = createEnum("dayOfWeek", java.time.DayOfWeek.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final TimePath<java.time.LocalTime> open = createTime("open", java.time.LocalTime.class);

    public final QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStoreHour(String variable) {
        this(StoreHour.class, forVariable(variable), INITS);
    }

    public QStoreHour(Path<? extends StoreHour> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreHour(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreHour(PathMetadata metadata, PathInits inits) {
        this(StoreHour.class, metadata, inits);
    }

    public QStoreHour(Class<? extends StoreHour> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}

