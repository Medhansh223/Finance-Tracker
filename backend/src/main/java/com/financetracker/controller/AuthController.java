package com.financetracker.controller;

import com.financetracker.constant.Constants;
import com.financetracker.dto.request.LoginRequest;
import com.financetracker.dto.request.SignupRequest;
import com.financetracker.dto.response.AuthResponse;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.service.AuthService;
import com.financetracker.util.Util;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Constants.AUTH_BASE_PATH)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest request) {
        MessageResponse response = authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(HttpServletRequest httpServletRequest) {
        String sessionToken = Util.extractBearerToken(httpServletRequest);
        MessageResponse response = authService.logoutUser(sessionToken);
        return ResponseEntity.ok(response);
    }
}
