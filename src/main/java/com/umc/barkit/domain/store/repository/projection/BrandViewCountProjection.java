package com.umc.barkit.domain.store.repository.projection;

public interface BrandViewCountProjection {
    Long getStoreBrandId();
    String getStoreBrandName();
    Long getTotalViewCount();
}