package com.financetracker.service;

import com.financetracker.dto.request.LoginRequest;
import com.financetracker.dto.request.SignupRequest;
import com.financetracker.dto.response.AuthResponse;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserShouldCreateUserWhenEmailIsAvailable() {
        SignupRequest request = new SignupRequest("Jane Doe", "jane@example.com", "secret123");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");

        MessageResponse response = authService.registerUser(request);

        assertEquals("User registered", response.message());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUserShouldThrowWhenEmailAlreadyExists() {
        SignupRequest request = new SignupRequest("Jane Doe", "jane@example.com", "secret123");
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUserShouldReturnSessionTokenForValidCredentials() {
        LoginRequest request = new LoginRequest("jane@example.com", "secret123");
        User user = buildUser();

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed-password")).thenReturn(true);
        when(sessionService.createSessionForUser(user)).thenReturn("session-token-123");

        AuthResponse response = authService.loginUser(request);

        assertEquals("session-token-123", response.sessionToken());
        assertEquals(1L, response.user().id());
        assertEquals("Jane Doe", response.user().name());
    }

    @Test
    void loginUserShouldThrowWhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("jane@example.com", "wrong-password");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> authService.loginUser(request));
        verify(sessionService, never()).createSessionForUser(any(User.class));
    }

    @Test
    void logoutUserShouldInvalidateSessionToken() {
        MessageResponse response = authService.logoutUser("session-token-123");

        assertEquals("Logged out", response.message());
        verify(sessionService).invalidateSessionToken("session-token-123");
    }

    private User buildUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setPassword("hashed-password");
        return user;
    }
}
