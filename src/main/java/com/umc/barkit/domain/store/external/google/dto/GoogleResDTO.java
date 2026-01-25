package com.umc.barkit.domain.store.external.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GoogleResDTO {

        //구글 id
        public record SearchTextResponse(
                List<Place> places
        ) {}

        public record Place(
                String id,
                List<Photo> photos
        ) {}

        public record Photo(
                String name,
                Integer widthPx,
                Integer heightPx
         ) {}
}
