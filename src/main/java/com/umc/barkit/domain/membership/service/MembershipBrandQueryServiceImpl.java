package com.umc.barkit.domain.membership.service;

import com.umc.barkit.domain.membership.converter.MembershipBrandConverter;
import com.umc.barkit.domain.membership.dto.response.MembershipBrandResponseDTO;
import com.umc.barkit.domain.membership.entity.MembershipBrand;
import com.umc.barkit.domain.membership.repository.MembershipBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipBrandQueryServiceImpl implements MembershipBrandQueryService {

    private final MembershipBrandRepository membershipBrandRepository;

    @Override
    public MembershipBrandResponseDTO.Top4BrandsDTO getTop4MembershipBrands() {
        List<MembershipBrand> top4Brands = membershipBrandRepository
                .findTop4ByRegistrationCount()
                .stream()
                .limit(4)
                .collect(Collectors.toList());

        return MembershipBrandConverter.toTop4BrandsDTO(top4Brands);
    }

    @Override
    public MembershipBrandResponseDTO.PopularBrandsDTO getPopularMembershipBrands() {
        List<MembershipBrand> popularBrands = membershipBrandRepository
                .findTop10ByRegistrationCount()
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        return MembershipBrandConverter.toPopularBrandsDTO(popularBrands);
    }
}