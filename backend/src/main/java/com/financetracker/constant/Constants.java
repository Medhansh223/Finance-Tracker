package com.financetracker.constant;

public final class Constants {

    // API paths
    public static final String API_BASE_PATH = "/api";
    public static final String AUTH_BASE_PATH = API_BASE_PATH + "/auth";
    public static final String TRANSACTIONS_BASE_PATH = API_BASE_PATH + "/transactions";

    // HTTP headers
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // Log messages
    public static final String USER_REGISTERED = "New user registered with email: {}";
    public static final String USER_LOGIN_SUCCESS = "User logged in successfully with email: {}";
    public static final String INVALID_LOGIN_ATTEMPT = "Invalid login attempt for email: {}";
    public static final String DUPLICATE_USER_REGISTRATION = "Registration failed because user already exists with email: {}";
    public static final String SESSION_CREATED = "Session created for user id: {}";
    public static final String SESSION_INVALIDATED = "Session invalidated for user id: {}";
    public static final String SESSION_EXPIRED = "Session expired for token";
    public static final String SESSION_NOT_FOUND = "Session not found for provided token";
    public static final String TRANSACTION_CREATED = "Transaction created with id: {} for user id: {}";
    public static final String TRANSACTION_UPDATED = "Transaction updated with id: {} for user id: {}";
    public static final String TRANSACTION_DELETED = "Transaction deleted with id: {} for user id: {}";
    public static final String TRANSACTION_NOT_FOUND = "Transaction not found with id: {} for user id: {}";
    public static final String UNAUTHORIZED_REQUEST = "Unauthorized request: {}";

    private Constants() {
    }
}
