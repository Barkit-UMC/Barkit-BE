package com.umc.barkit.domain.store.entity.mapping;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QStoreFacility is a Querydsl query type for StoreFacility
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreFacility extends EntityPathBase<StoreFacility> {

    private static final long serialVersionUID = -261857355L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QStoreFacility storeFacility = new QStoreFacility("storeFacility");

    public final com.umc.barkit.domain.store.entity.QFacility facility;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.umc.barkit.domain.store.entity.QStore store;

    public QStoreFacility(String variable) {
        this(StoreFacility.class, forVariable(variable), INITS);
    }

    public QStoreFacility(Path<? extends StoreFacility> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QStoreFacility(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QStoreFacility(PathMetadata metadata, PathInits inits) {
        this(StoreFacility.class, metadata, inits);
    }

    public QStoreFacility(Class<? extends StoreFacility> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.facility = inits.isInitialized("facility") ? new com.umc.barkit.domain.store.entity.QFacility(forProperty("facility")) : null;
        this.store = inits.isInitialized("store") ? new com.umc.barkit.domain.store.entity.QStore(forProperty("store"), inits.get("store")) : null;
    }

}

