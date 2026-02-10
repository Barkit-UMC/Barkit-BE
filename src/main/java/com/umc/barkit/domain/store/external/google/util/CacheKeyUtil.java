package com.umc.barkit.domain.store.external.google.util;

import com.umc.barkit.domain.store.util.StoreUtil;

public class CacheKeyUtil {
    private CacheKeyUtil() {}

    public static String nearKey(String query, double lat, double lng) {
        String nq = StoreUtil.searchNormalize(query);
        double lat3 = round(lat, 3);
        double lng3 = round(lng, 3);
        return "q=" + nq + ":lat=" + lat3 + ":lng=" + lng3;
    }

    public static String globalKey(String query) {
        String nq = StoreUtil.searchNormalize(query);
        return "q=" + nq;
    }

    private static double round(double v, int scale) {
        double p = Math.pow(10, scale);
        return Math.round(v * p) / p;
    }
}

