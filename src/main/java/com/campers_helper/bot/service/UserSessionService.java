package com.campers_helper.bot.service;

import com.campers_helper.bot.model.UserSession;
import com.campers_helper.bot.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSessionService {
    private final UserSessionRepository userSessionRepository;

    public UserSession getOrCreate(Long chatId) {
        Optional<UserSession> userSession = userSessionRepository.findByChatId(chatId);
        return userSession.orElseGet(() -> {
            UserSession newSession = new UserSession();
            newSession.setChatId(chatId);
            newSession.setLocale("en");
            return userSessionRepository.save(newSession);
        });
    }

    public Locale getLocale(Long chatId) {
        return Locale.forLanguageTag(getOrCreate(chatId).getLocale());
    }

    @Transactional
    public void save(UserSession session) {
        userSessionRepository.save(session);
    }
}