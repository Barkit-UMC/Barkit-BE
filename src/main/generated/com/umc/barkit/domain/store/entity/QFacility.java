package com.umc.barkit.domain.store.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QFacility is a Querydsl query type for Facility
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFacility extends EntityPathBase<Facility> {

    private static final long serialVersionUID = 1361469650L;

    public static final QFacility facility = new QFacility("facility");

    public final com.umc.barkit.global.entity.QBaseEntity _super = new com.umc.barkit.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QFacility(String variable) {
        super(Facility.class, forVariable(variable));
    }

    public QFacility(Path<? extends Facility> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFacility(PathMetadata metadata) {
        super(Facility.class, metadata);
    }

}

