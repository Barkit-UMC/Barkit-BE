package com.umc.barkit.domain.user.dto.res;

import java.util.List;

public class TermResponseDto {
    public record TermItem(
            Long termId,
            String content,
            Boolean isRequired
    ){}

    public record TermListResponse(
            List<TermItem> terms
    ){}
}
