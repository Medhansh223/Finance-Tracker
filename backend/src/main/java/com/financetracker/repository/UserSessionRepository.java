package com.financetracker.repository;

import com.financetracker.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserId(Long userId);
}
