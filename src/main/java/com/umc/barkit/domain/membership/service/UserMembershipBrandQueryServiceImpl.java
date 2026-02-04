package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.UserMembershipBrandConverter;

import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;

import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.exception.code.MembershipErrorCode;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import com.umc.barkit.domain.store.repository.StoreBrandMembershipBrandRepository;
import com.umc.barkit.domain.store.entity.Store;
import com.umc.barkit.domain.store.repository.StoreRepository;
import com.umc.barkit.global.apiPayload.code.BaseErrorCode;
import com.umc.barkit.domain.store.external.google.GoogleMapSearchClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

import com.umc.barkit.domain.membership.exception.MembershipException;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMembershipBrandQueryServiceImpl implements UserMembershipBrandQueryService {

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final UserMembershipBrandConverter userMembershipBrandConverter;
    private final MembershipBrandRepository membershipBrandRepository;
    private final StoreBrandMembershipBrandRepository storeBrandMembershipBrandRepository;
    private final StoreRepository storeRepository;
    private final GoogleMapSearchClient googleMapSearchClient;

    @Override
    public UserMembershipBrandResponseDTO.SearchResultDTO searchUserMembershipBrands(
            Long userId,
            String keyword,
            Long cursor,
            Integer limit
    ) {
        // 1. limit 기본값 설정
        int pageSize = (limit != null && limit > 0) ? limit : 20;

        // 2. DB 조회
        List<UserMembershipBrand> brands = userMembershipBrandRepository
                .searchByUserIdAndKeyword(userId, keyword, cursor, pageSize);

        // 3. hasNext 확인
        boolean hasNext = brands.size() > pageSize;

        // 4. 실제 반환할 데이터
        List<UserMembershipBrand> resultBrands = hasNext
                ? brands.subList(0, pageSize)
                : brands;

        // 5. nextCursor 계산
        Long nextCursor = hasNext
                ? resultBrands.get(resultBrands.size() - 1).getId()
                : null;

        // 6. DTO 변환 및 반환
        return userMembershipBrandConverter.toSearchResultDTO(resultBrands, nextCursor, hasNext);
    }


    public UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO getUserMembershipBarcode(Long userId,Long membershipBrandId){

        MembershipBrand brand = membershipBrandRepository.findById(membershipBrandId)
                .orElseThrow(() -> new MembershipException(MembershipErrorCode.BRAND4001));


        Optional<UserMembershipBrand> umbOpt =
                userMembershipBrandRepository
                        .findByUserIdAndMembershipBrandId(userId, membershipBrandId);


        if (umbOpt.isEmpty()) {
            return UserMembershipBrandResponseDTO.UserMembershipBarcodeDTO.builder()
                    .membershipNumber("")
                    .logoUrl(brand.getLogoUrl())
                    .brandName(brand.getName())
                    .build();
        }

        return userMembershipBrandConverter.toBarcodeDTO(umbOpt.get());
    }

    @Override
    public UserMembershipBrandResponseDTO.AvailableStoreListDTO
    getAvailableStores(Long userId, Long userMembershipBrandId, String keyword) {

        // 1. 사용자 멤버십 검증
        UserMembershipBrand umb =
                userMembershipBrandRepository.findById(userMembershipBrandId)
                        .orElseThrow(() ->
                                new MembershipException(MembershipErrorCode.MEMBERSHIP4001));

        if (!umb.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorCode.MEMBERSHIP4003);
        }

        // 2. 멤버십 브랜드 ID
        Long membershipBrandId = umb.getMembershipBrandId();

        // 3. 멤버십 → StoreBrand ID 목록
        List<Long> storeBrandIds =
                storeBrandMembershipBrandRepository
                        .findStoreBrandIdsByMembershipBrandId(membershipBrandId);

        if (storeBrandIds.isEmpty()) {
            return UserMembershipBrandResponseDTO.AvailableStoreListDTO.builder()
                    .stores(List.of())
                    .build();
        }

        // 4. Store 조회
        List<Store> stores =
                storeRepository.findStoresByStoreBrandIdsAndKeyword(
                        storeBrandIds, keyword
                );

        // 5. Google Place 연동 + DTO 변환 (🔥 핵심)
        List<UserMembershipBrandResponseDTO.AvailableStoreDTO> storeDTOs =
                stores.stream()
                        .map(store -> {
                            var place =
                                    googleMapSearchClient
                                            .getPlaceDetail(store.getGoogleId());

                            return userMembershipBrandConverter
                                    .toAvailableStoreDTO(store, place);
                        })
                        .toList();

        return UserMembershipBrandResponseDTO.AvailableStoreListDTO.builder()
                .stores(storeDTOs)
                .build();
    }
}
