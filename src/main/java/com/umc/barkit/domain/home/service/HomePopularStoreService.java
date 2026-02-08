package com.umc.barkit.domain.home.service;

import com.umc.barkit.domain.home.dto.response.HomePopularStoreResponse;

public interface HomePopularStoreService {
    HomePopularStoreResponse getPopularStores(Long userMembershipBrandId);
}