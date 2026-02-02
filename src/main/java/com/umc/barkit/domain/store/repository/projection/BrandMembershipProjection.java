package com.umc.barkit.domain.store.repository.projection;

public interface BrandMembershipProjection {
    Long getStoreBrandId();
    Long getMembershipId();
    String getMembershipName();
    String getMembershipLogoUrl();
}
