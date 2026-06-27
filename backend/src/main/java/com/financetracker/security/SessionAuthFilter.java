package com.financetracker.security;

import com.financetracker.constant.Constants;
import com.financetracker.service.SessionService;
import com.financetracker.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SessionAuthFilter.class);

    private final SessionService sessionService;

    public SessionAuthFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String sessionToken = Util.extractBearerToken(request);
            if (sessionToken == null) {
                logger.warn(Constants.UNAUTHORIZED_REQUEST, "Missing session token");
                writeUnauthorizedResponse(response, "No session token provided");
                return;
            }

            Long userId = sessionService.validateSessionToken(sessionToken);
            UserContext.setCurrentUserId(userId);
            filterChain.doFilter(request, response);
        } catch (SecurityException exception) {
            logger.warn(Constants.UNAUTHORIZED_REQUEST, exception.getMessage());
            writeUnauthorizedResponse(response, exception.getMessage());
        } finally {
            UserContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.equals("/")
                || path.startsWith(Constants.AUTH_BASE_PATH + "/signup")
                || path.startsWith(Constants.AUTH_BASE_PATH + "/login");
    }

    private void writeUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
