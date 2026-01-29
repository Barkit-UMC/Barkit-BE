package com.umc.barkit.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QStoreBrand is a Querydsl query type for StoreBrand
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QStoreBrand extends EntityPathBase<StoreBrand> {

    private static final long serialVersionUID = -1234310763L;

    public static final QStoreBrand storeBrand = new QStoreBrand("storeBrand");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    public final EnumPath<com.umc.barkit.domain.store.enums.Category> category = createEnum("category", com.umc.barkit.domain.store.enums.Category.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QStoreBrand(String variable) {
        super(StoreBrand.class, forVariable(variable));
    }

    public QStoreBrand(Path<? extends StoreBrand> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStoreBrand(PathMetadata metadata) {
        super(StoreBrand.class, metadata);
    }

}

