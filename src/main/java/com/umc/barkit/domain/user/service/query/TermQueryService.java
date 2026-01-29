package com.umc.barkit.domain.user.service.query;

import com.umc.barkit.domain.user.converter.TermConverter;
import com.umc.barkit.domain.user.dto.res.TermResponseDto;
import com.umc.barkit.domain.user.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermQueryService {

    private final TermRepository termRepository;

    // 약관 목록 조회
    @Transactional(readOnly = true)
    public TermResponseDto.TermListResponse getTerms(){
        return TermConverter.toListResponse(termRepository.findActiveLatestTerms());
    }
}
