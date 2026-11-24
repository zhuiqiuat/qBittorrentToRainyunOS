package cn.zqat.qbittorrenttorainyunos.utils;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTUtilsTest {

    private JWTUtils jwtUtils;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils();
        String secret = Base64.getEncoder().encodeToString(
                "unit-test-secret-key-at-least-32-bytes-long".getBytes(StandardCharsets.UTF_8)
        );
        ReflectionTestUtils.setField(jwtUtils, "secret", secret);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 60_000L);
    }

    @Test
    void createsAndParsesTokenWithExpectedClaims() {
        String token = jwtUtils.getToken(7, "alice");

        Claims claims = jwtUtils.extractAllClaims(token);

        assertEquals(7, claims.get("id", Integer.class));
        assertEquals("alice", claims.get("username", String.class));
        assertTrue(jwtUtils.isValid(token));
    }

    @Test
    void rejectsMalformedToken() {
        assertFalse(jwtUtils.isValid("not-a-jwt"));
    }

    @Test
    void rejectsExpiredToken() {
        ReflectionTestUtils.setField(jwtUtils, "expiration", -1_000L);

        String token = jwtUtils.getToken(7, "alice");

        assertFalse(jwtUtils.isValid(token));
    }
}
