package com.umc.barkit.domain.store.external.google.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GoogleResDTO {


    public record SearchTextResponse(
            List<Place> places
    ) {}


    public record Place(
            String id,
            DisplayName displayName,
            String formattedAddress,
            String nationalPhoneNumber,
            LatLng location
    ) {}

    public record DisplayName(
            String text
    ) {}

    public record LatLng(
            double latitude,
            double longitude
    ) {}
}
