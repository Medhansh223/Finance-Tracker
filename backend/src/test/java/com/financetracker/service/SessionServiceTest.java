package com.financetracker.service;

import com.financetracker.config.AppProperties;
import com.financetracker.entity.User;
import com.financetracker.entity.UserSession;
import com.financetracker.repository.UserSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private SessionService sessionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setName("Test User");
    }

    private void stubSessionConfiguration() {
        AppProperties.Session sessionConfig = new AppProperties.Session();
        sessionConfig.setExpiryHours(24);
        when(appProperties.getSession()).thenReturn(sessionConfig);
    }

    @Test
    void createSessionForUserShouldPersistSessionAndReturnToken() {
        stubSessionConfiguration();
        when(userSessionRepository.save(any(UserSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String token = sessionService.createSessionForUser(user);

        assertNotNull(token);

        ArgumentCaptor<UserSession> sessionCaptor = ArgumentCaptor.forClass(UserSession.class);
        verify(userSessionRepository).deleteByUserId(1L);
        verify(userSessionRepository).save(sessionCaptor.capture());

        UserSession savedSession = sessionCaptor.getValue();
        assertEquals(user, savedSession.getUser());
        assertEquals(token, savedSession.getToken());
        assertNotNull(savedSession.getExpiresAt());
    }

    @Test
    void validateSessionTokenShouldReturnAuthenticatedUserForValidToken() {
        UserSession session = buildActiveSession("valid-token");

        when(userSessionRepository.findByToken("valid-token")).thenReturn(Optional.of(session));

        Long userId = sessionService.validateSessionToken("valid-token");

        assertEquals(1L, userId);
    }

    @Test
    void validateSessionTokenShouldThrowWhenTokenIsMissing() {
        when(userSessionRepository.findByToken("missing-token")).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () -> sessionService.validateSessionToken("missing-token"));
    }

    @Test
    void validateSessionTokenShouldThrowWhenSessionIsExpired() {
        UserSession expiredSession = buildActiveSession("expired-token");
        expiredSession.setExpiresAt(Instant.now().minus(1, ChronoUnit.HOURS));

        when(userSessionRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredSession));

        assertThrows(SecurityException.class, () -> sessionService.validateSessionToken("expired-token"));
    }

    @Test
    void invalidateSessionTokenShouldDeleteExistingSession() {
        UserSession session = buildActiveSession("logout-token");
        when(userSessionRepository.findByToken("logout-token")).thenReturn(Optional.of(session));

        sessionService.invalidateSessionToken("logout-token");

        verify(userSessionRepository).deleteByToken("logout-token");
    }

    private UserSession buildActiveSession(String token) {
        UserSession session = new UserSession();
        session.setToken(token);
        session.setUser(user);
        session.setExpiresAt(Instant.now().plus(1, ChronoUnit.DAYS));
        return session;
    }
}
