package com.financetracker.service;

import com.financetracker.constant.Constants;
import com.financetracker.dto.request.LoginRequest;
import com.financetracker.dto.request.SignupRequest;
import com.financetracker.dto.response.AuthResponse;
import com.financetracker.dto.response.MessageResponse;
import com.financetracker.dto.response.UserResponse;
import com.financetracker.entity.User;
import com.financetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            SessionService sessionService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MessageResponse registerUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            logger.warn(Constants.DUPLICATE_USER_REGISTRATION, request.email());
            throw new IllegalStateException("User already exists");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        logger.info(Constants.USER_REGISTERED, request.email());
        return new MessageResponse("User registered");
    }

    @Transactional
    public AuthResponse loginUser(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .filter(foundUser -> passwordEncoder.matches(request.password(), foundUser.getPassword()))
                .orElseThrow(() -> {
                    logger.warn(Constants.INVALID_LOGIN_ATTEMPT, request.email());
                    return new IllegalArgumentException("Invalid credentials");
                });

        String sessionToken = sessionService.createSessionForUser(user);
        logger.info(Constants.USER_LOGIN_SUCCESS, request.email());

        return new AuthResponse(
                sessionToken,
                new UserResponse(user.getId(), user.getName(), user.getEmail())
        );
    }

    @Transactional
    public MessageResponse logoutUser(String sessionToken) {
        sessionService.invalidateSessionToken(sessionToken);
        return new MessageResponse("Logged out");
    }
}
