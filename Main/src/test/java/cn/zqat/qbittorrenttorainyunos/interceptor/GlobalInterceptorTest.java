package cn.zqat.qbittorrenttorainyunos.interceptor;

import cn.zqat.qbittorrenttorainyunos.pojo.LoginRequest;
import cn.zqat.qbittorrenttorainyunos.pojo.Result;
import cn.zqat.qbittorrenttorainyunos.pojo.User;
import cn.zqat.qbittorrenttorainyunos.service.UserService;
import cn.zqat.qbittorrenttorainyunos.utils.JWTUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalInterceptorTest {

    private JWTUtils jwtUtils;
    private StubUserService userService;
    private GlobalInterceptor interceptor;

    @BeforeEach
    void setUp() {
        jwtUtils = new JWTUtils();
        String secret = Base64.getEncoder().encodeToString(
                "unit-test-secret-key-at-least-32-bytes-long".getBytes(StandardCharsets.UTF_8)
        );
        ReflectionTestUtils.setField(jwtUtils, "secret", secret);
        ReflectionTestUtils.setField(jwtUtils, "expiration", 60_000L);

        userService = new StubUserService();
        interceptor = new GlobalInterceptor();
        ReflectionTestUtils.setField(interceptor, "jwtu", jwtUtils);
        ReflectionTestUtils.setField(interceptor, "usrs", userService);
    }

    @Test
    void rejectsRequestWithoutToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertStatusUnauthorized(response);
    }

    @Test
    void rejectsInvalidToken() {
        MockHttpServletRequest request = requestWithToken("invalid");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertStatusUnauthorized(response);
    }

    @Test
    void rejectsTokenThatDoesNotMatchStoredToken() {
        String staleToken = jwtUtils.getToken(12, "alice");
        MockHttpServletRequest request = requestWithToken(staleToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        userService.storedToken = "newer-token";

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertStatusUnauthorized(response);
    }

    @Test
    void acceptsTokenThatMatchesStoredToken() {
        String validToken = jwtUtils.getToken(12, "alice");
        MockHttpServletRequest request = requestWithToken(validToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        userService.storedToken = validToken;

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(12, userService.requestedUserId);
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("token", token);
        return request;
    }

    private void assertStatusUnauthorized(MockHttpServletResponse response) {
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    }

    private static class StubUserService implements UserService {

        private String storedToken;
        private Integer requestedUserId;

        @Override
        public Result login(LoginRequest loginRequest) {
            throw new UnsupportedOperationException();
        }

        @Override
        public User getUserById(Integer id) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getTokenById(Integer id) {
            requestedUserId = id;
            return storedToken;
        }
    }
}
