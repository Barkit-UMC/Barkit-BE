package com.umc.barkit.domain.store.external.google.service;

import com.umc.barkit.domain.store.external.google.GoogleMapSearchClient;
import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleSearchCacheService {

    private final GoogleMapSearchClient googleClient;

    @Cacheable(
            cacheNames = "googleSearchNear",
            key = "T(com.umc.barkit.domain.store.external.google.util.CacheKeyUtil).nearKey(#query, #lat, #lng)",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<GoogleResDTO.Place> searchNear(String query, double lat, double lng) {
        log.info("[CACHE MISS] googleSearchNear query={}", query);
        return googleClient.searchText(query, lat, lng);
    }

    @Cacheable(
            cacheNames = "googleSearchGlobal",
            key = "T(com.umc.barkit.domain.store.external.google.util.CacheKeyUtil).globalKey(#query)",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<GoogleResDTO.Place> searchGlobal(String query) {
        log.info("[CACHE MISS] googleSearchGlobal query={}", query);
        return googleClient.searchText(query);
    }
}

