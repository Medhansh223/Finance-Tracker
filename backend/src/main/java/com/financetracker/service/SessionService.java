package com.financetracker.service;

import com.financetracker.config.AppProperties;
import com.financetracker.constant.Constants;
import com.financetracker.entity.User;
import com.financetracker.entity.UserSession;
import com.financetracker.repository.UserSessionRepository;
import com.financetracker.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class SessionService {

    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);

    private final UserSessionRepository userSessionRepository;
    private final AppProperties appProperties;

    public SessionService(UserSessionRepository userSessionRepository, AppProperties appProperties) {
        this.userSessionRepository = userSessionRepository;
        this.appProperties = appProperties;
    }

    @Transactional
    public String createSessionForUser(User user) {
        userSessionRepository.deleteByUserId(user.getId());

        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(Util.generateSessionToken());
        session.setExpiresAt(calculateExpiryTime());

        userSessionRepository.save(session);
        logger.info(Constants.SESSION_CREATED, user.getId());
        return session.getToken();
    }

    @Transactional(readOnly = true)
    public Long validateSessionToken(String token) {
        UserSession session = userSessionRepository.findByToken(token)
                .orElseThrow(() -> {
                    logger.warn(Constants.SESSION_NOT_FOUND);
                    return new SecurityException("Invalid or expired session token");
                });

        if (session.isExpired()) {
            logger.warn(Constants.SESSION_EXPIRED);
            throw new SecurityException("Invalid or expired session token");
        }

        return session.getUser().getId();
    }

    @Transactional
    public void invalidateSessionToken(String token) {
        userSessionRepository.findByToken(token).ifPresent(session -> {
            userSessionRepository.deleteByToken(token);
            logger.info(Constants.SESSION_INVALIDATED, session.getUser().getId());
        });
    }

    private Instant calculateExpiryTime() {
        return Instant.now().plus(appProperties.getSession().getExpiryHours(), ChronoUnit.HOURS);
    }
}
