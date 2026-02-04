package com.umc.barkit.domain.store.util;

import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StoreUtil {

//    private StoreUtil() {}

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double round2(double v) {
        return Math.round(v * 100) / 100.0;
    }

    //위도, 경도로 사용자 현재위치와 매장 사이의 거리 계산
    public static double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }

    //구글 길찾기 url 반환
    public static String buildGoogleDirectionUrl(String placeId, double lat, double lng) {
        return "https://www.google.com/maps/dir/?api=1"
                + "&destination=" + lat + "," + lng
                + "&destination_place_id=" + java.net.URLEncoder.encode(placeId, java.nio.charset.StandardCharsets.UTF_8);
    }

    //검색어 정규화
    public static String searchNormalize(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");
    }
}
