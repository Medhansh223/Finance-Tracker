package com.financetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financetracker.dto.request.LoginRequest;
import com.financetracker.dto.request.SignupRequest;
import com.financetracker.dto.response.AuthResponse;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.dto.response.UserResponse;
import com.financetracker.exception.GlobalExceptionHandler;
import com.financetracker.service.AuthService;
import com.financetracker.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private SessionService sessionService;

    @Test
    void signupShouldReturnCreatedResponse() throws Exception {
        SignupRequest request = new SignupRequest("Jane Doe", "jane@example.com", "secret123");
        when(authService.registerUser(any(SignupRequest.class)))
                .thenReturn(new MessageResponse("User registered"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered"));
    }

    @Test
    void loginShouldReturnSessionToken() throws Exception {
        LoginRequest request = new LoginRequest("jane@example.com", "secret123");
        AuthResponse response = new AuthResponse(
                "session-token-123",
                new UserResponse(1L, "Jane Doe", "jane@example.com")
        );
        when(authService.loginUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionToken").value("session-token-123"))
                .andExpect(jsonPath("$.user.email").value("jane@example.com"));
    }
}
