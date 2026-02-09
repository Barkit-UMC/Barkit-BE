package com.umc.barkit.global.auth.oauth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Component;

@Component
public class OAuthStateUtil {
    private static final long TTL_SECONDS = 300;

    public String generate(String secret) {
        long ts = Instant.now().getEpochSecond();
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String payload = ts + "." + nonce;
        String sig = hmacSha256Url(secret, payload);
        return base64Url(payload) + "." + sig;
    }

    public void verify(String secret, String state) {
        String[] parts = state.split("\\.");
        if (parts.length != 2) throw new IllegalArgumentException("INVALID_STATE");

        String payload = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
        String sig = parts[1];

        String expected = hmacSha256Url(secret, payload);
        if (!MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), sig.getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("INVALID_STATE");
        }

        String[] p = payload.split("\\.");
        if (p.length != 2) throw new IllegalArgumentException("INVALID_STATE");

        long ts = Long.parseLong(p[0]);
        long now = Instant.now().getEpochSecond();
        if (now - ts > TTL_SECONDS) throw new IllegalArgumentException("STATE_EXPIRED");
    }

    private String hmacSha256Url(String secret, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] out = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String base64Url(String s) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
}
