package com.financetracker.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UtilTest {

    @Test
    void extractBearerTokenShouldReturnTokenWhenHeaderIsValid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer session-token-123");

        assertEquals("session-token-123", Util.extractBearerToken(request));
    }

    @Test
    void extractBearerTokenShouldReturnNullWhenHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(Util.extractBearerToken(request));
    }

    @Test
    void extractBearerTokenShouldReturnNullWhenHeaderFormatIsInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic credentials");

        assertNull(Util.extractBearerToken(request));
    }

    @Test
    void generateSessionTokenShouldReturnNonEmptyHexString() {
        String token = Util.generateSessionToken();

        assertNotNull(token);
        assertEquals(64, token.length());
    }
}
