package com.umc.barkit.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStorePhoto is a Querydsl query type for StorePhoto
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStorePhoto extends EntityPathBase<StorePhoto> {

    private static final long serialVersionUID = -1221665728L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStorePhoto storePhoto = new QStorePhoto("storePhoto");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath reference = createString("reference");

    public final QStore store;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public QStorePhoto(String variable) {
        this(StorePhoto.class, forVariable(variable), INITS);
    }

    public QStorePhoto(Path<? extends StorePhoto> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStorePhoto(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStorePhoto(PathMetadata metadata, PathInits inits) {
        this(StorePhoto.class, metadata, inits);
    }

    public QStorePhoto(Class<? extends StorePhoto> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.store = inits.isInitialized("store") ? new QStore(forProperty("store"), inits.get("store")) : null;
    }

}

