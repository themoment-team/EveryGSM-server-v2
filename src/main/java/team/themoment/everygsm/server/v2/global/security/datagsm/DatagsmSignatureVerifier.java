package team.themoment.everygsm.server.v2.global.security.datagsm;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatagsmSignatureVerifier {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;

    public DatagsmSignatureVerifier(@Value("${datagsm.event.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("datagsm.event.secret이 설정되지 않았습니다. 서명 검증을 위해 반드시 값을 지정해야 합니다.");
        }
        this.secret = secret;
    }

    public boolean isValid(String payload, String signature) {
        if (signature == null || signature.isBlank()) {
            return false;
        }

        byte[] expected = hmacSha256(payload);
        byte[] provided = hexToBytes(signature);
        if (provided == null || provided.length != expected.length) {
            return false;
        }

        return MessageDigest.isEqual(expected, provided);
    }

    private byte[] hmacSha256(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 서명 계산에 실패했습니다.", e);
        }
    }

    private byte[] hexToBytes(String hex) {
        String normalized = hex.strip();
        if (normalized.length() % 2 != 0) {
            return null;
        }
        try {
            byte[] result = new byte[normalized.length() / 2];
            for (int i = 0; i < result.length; i++) {
                int index = i * 2;
                result[i] = (byte) Integer.parseInt(normalized.substring(index, index + 2), 16);
            }
            return result;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
