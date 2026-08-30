package team.themoment.everygsm.server.v2.global.security.datagsm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DataGSM 서명 검증기 테스트")
class DatagsmSignatureVerifierTest {

    private static final String SECRET = "test-secret";

    private final DatagsmSignatureVerifier verifier = new DatagsmSignatureVerifier(SECRET);

    private String hmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : raw) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Nested
    @DisplayName("생성자는")
    class Describe_constructor {

        @Nested
        @DisplayName("secret이 비어있는 경우")
        class Context_with_blank_secret {

            @Test
            @DisplayName("IllegalStateException을 던진다")
            void it_throws_illegal_state_exception() {
                assertThrows(IllegalStateException.class, () -> new DatagsmSignatureVerifier(""));
            }
        }

        @Nested
        @DisplayName("secret이 null인 경우")
        class Context_with_null_secret {

            @Test
            @DisplayName("IllegalStateException을 던진다")
            void it_throws_illegal_state_exception() {
                assertThrows(IllegalStateException.class, () -> new DatagsmSignatureVerifier(null));
            }
        }
    }

    @Nested
    @DisplayName("isValid 메서드는")
    class Describe_isValid {

        @Nested
        @DisplayName("올바른 서명이 주어진 경우")
        class Context_with_correct_signature {

            @Test
            @DisplayName("true를 반환한다")
            void it_returns_true() {
                String payload = "{\"event\":\"project.updated\"}";

                assertTrue(verifier.isValid(payload, hmac(payload)));
            }
        }

        @Nested
        @DisplayName("변조된 payload가 주어진 경우")
        class Context_with_tampered_payload {

            @Test
            @DisplayName("false를 반환한다")
            void it_returns_false() {
                String payload = "{\"event\":\"project.updated\"}";
                String signatureForOtherPayload = hmac("{\"event\":\"project.created\"}");

                assertFalse(verifier.isValid(payload, signatureForOtherPayload));
            }
        }

        @Nested
        @DisplayName("서명이 비어있는 경우")
        class Context_with_blank_signature {

            @Test
            @DisplayName("false를 반환한다")
            void it_returns_false() {
                assertFalse(verifier.isValid("payload", ""));
            }
        }

        @Nested
        @DisplayName("서명이 16진수 형식이 아닌 경우")
        class Context_with_non_hex_signature {

            @Test
            @DisplayName("false를 반환한다")
            void it_returns_false() {
                assertFalse(verifier.isValid("payload", "not-a-valid-hex-signature"));
            }
        }
    }
}
