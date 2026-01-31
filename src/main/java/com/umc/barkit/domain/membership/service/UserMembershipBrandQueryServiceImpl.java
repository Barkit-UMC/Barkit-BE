package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.UserMembershipBrandConverter;
import com.umc.barkit.domain.membership.dto.response.UserMembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.UserMembershipBrand;
import com.umc.barkit.domain.membership.repository.UserMembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMembershipBrandQueryServiceImpl implements UserMembershipBrandQueryService {

    private final UserMembershipBrandRepository userMembershipBrandRepository;
    private final UserMembershipBrandConverter userMembershipBrandConverter;

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
}