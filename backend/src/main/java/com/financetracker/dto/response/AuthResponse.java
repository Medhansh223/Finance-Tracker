package com.financetracker.dto.response;

public record AuthResponse(
        String sessionToken,
        UserResponse user
) {
}
