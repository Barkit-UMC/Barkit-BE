package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.MembershipBrandConverter;
import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipBrandQueryServiceImpl implements MembershipBrandQueryService {

    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public MembershipBrandResponseDTO.DefaultBrandsDTO getDefaultMembershipBrands() {
        List<MembershipBrand> defaultBrands = membershipBrandRepository.findTop10ByRegistrationCountForDefault();
        return MembershipBrandConverter.toDefaultBrandsDTO(defaultBrands);
    }

    @Override
    public MembershipBrandResponseDTO.PopularBrandsDTO getPopularMembershipBrands() {
        List<MembershipBrand> popularBrands = membershipBrandRepository.findTop10ByRegistrationCount();
        return MembershipBrandConverter.toPopularBrandsDTO(popularBrands);
    }

    @Override
    public MembershipBrandResponseDTO.SearchResultDTO searchMembershipBrands(
            String keyword,
            Long cursor,
            Integer limit
    ) {
        // 1. limit 기본값 설정
        int pageSize = (limit != null && limit > 0) ? limit : 20;

        // 2. DB 조회 (limit + 1개 조회하여 hasNext 확인)
        List<MembershipBrand> brands = membershipBrandRepository
                .searchByKeyword(keyword, cursor, pageSize);

        // 3. hasNext 확인
        boolean hasNext = brands.size() > pageSize;

        // 4. 실제 반환할 데이터
        List<MembershipBrand> resultBrands = hasNext
                ? brands.subList(0, pageSize)
                : brands;

        // 5. nextCursor 계산
        Long nextCursor = hasNext
                ? resultBrands.get(resultBrands.size() - 1).getId()
                : null;

        // 6. DTO 변환 및 반환
        return MembershipBrandConverter.toSearchResultDTO(resultBrands, nextCursor, hasNext);
    }
}