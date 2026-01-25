package com.umc.barkit.domain.store.external.google;

import com.umc.barkit.domain.store.external.google.dto.GoogleResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleMapSearchClient {

    private final WebClient googleWebClient;

    @Value("${google.api.key}")
    private String apiKey;

    public List<GoogleResDTO.Place> searchText(String textQuery) {

        GoogleResDTO.SearchTextResponse response =
                googleWebClient.post()
                        .uri("/v1/places:searchText")
                        .header("X-Goog-Api-Key", apiKey)
                        .header(
                                "X-Goog-FieldMask",
                                "places.id,places.displayName,places.photos"
                        )
                        .bodyValue(Map.of(
                                "textQuery", textQuery,
                                "languageCode", "ko"
                        ))
                        .retrieve()
                        .bodyToMono(GoogleResDTO.SearchTextResponse.class)
                        .block();

        if (response == null || response.places() == null) {
            return List.of();
        }
        return response.places();
    }

    public String getThumbnailPhotoUrl(GoogleResDTO.Place place) {
        if (place.photos() == null || place.photos().isEmpty()) {
            return null;
        }
        String photoName = place.photos().get(0).name();
        return buildPhotoMediaUrl(photoName, 800);
    }

    private String buildPhotoMediaUrl(String photoName, int maxWidthPx) {
        return "https://places.googleapis.com/v1/" + photoName + "/media"
                + "?maxWidthPx=" + maxWidthPx
                + "&key=" + apiKey;
    }
}
