package com.umc.barkit.domain.user.converter;

import com.umc.barkit.domain.user.dto.res.TermResponseDto;
import com.umc.barkit.domain.user.dto.res.TermResponseDto.TermItem;
import com.umc.barkit.domain.user.entity.Term;
import java.util.List;

public class TermConverter {

    // Entity -> DTO
    public static TermResponseDto.TermItem toItem(Term term) {
        return new TermResponseDto.TermItem(
                term.getId(),
                term.getContent(),
                term.getIsRequired()
        );
    }


    // Entity -> DTO
    public static TermResponseDto.TermListResponse toListResponse(List<Term> terms) {
        List<TermItem> items = terms.stream()
                .map(TermConverter::toItem)
                .toList();

        return new TermResponseDto.TermListResponse(items);
    }
}
