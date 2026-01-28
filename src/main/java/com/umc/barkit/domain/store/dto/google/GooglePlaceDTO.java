package com.umc.barkit.domain.store.dto.google;

import java.util.List;
import lombok.Builder;

public class GooglePlaceDTO {

    @Builder
    public record Place(
            String id,
            DisplayName displayName,
            String formattedAddress,
            String nationalPhoneNumber,
            String websiteUri,
            Location location,
            GoogleOpeningHours currentOpeningHours,
            List<GooglePhoto> photos
    ) {}

    public record DisplayName(
            String text,
            String languageCode
    ) {}

    public record Location(
            Double latitude,
            Double longitude
    ) {}

    public record GoogleOpeningHours(
            Boolean openNow,
            List<String> weekdayDescriptions
    ) {}

    public record GooglePhoto(
            String name,
            Integer widthPx,
            Integer heightPx
    ) {}
}
