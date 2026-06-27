package com.financetracker.util;

import com.financetracker.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;

import java.security.SecureRandom;
import java.util.HexFormat;

public final class Util {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 32;

    private Util() {
    }

    public static String extractBearerToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(Constants.AUTHORIZATION_HEADER);
        if (authorizationHeader == null || !authorizationHeader.startsWith(Constants.BEARER_PREFIX)) {
            return null;
        }
        return authorizationHeader.substring(Constants.BEARER_PREFIX.length()).trim();
    }

    public static String generateSessionToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return HexFormat.of().formatHex(tokenBytes);
    }
}
