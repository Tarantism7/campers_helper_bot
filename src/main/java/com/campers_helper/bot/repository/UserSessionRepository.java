package com.campers_helper.bot.repository;

import com.campers_helper.bot.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSession,Long> {
    Optional<UserSession> findByChatId(Long chatId);
}
