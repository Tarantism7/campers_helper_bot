package com.campers_helper.bot.commands;

import com.campers_helper.bot.events.MessageEvent;
import com.campers_helper.bot.model.UserSession;
import com.campers_helper.bot.service.KeyboardService;
import com.campers_helper.bot.service.LocalizationService;
import com.campers_helper.bot.service.MessageTrackerService;
import com.campers_helper.bot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LanguageCallbackCommand implements Command {
    private static final String LANG_PREFIX = "lang_";

    private final ApplicationEventPublisher eventPublisher;
    private final LocalizationService localizationService;
    private final MessageTrackerService messageTrackerService;
    private final UserSessionService userSessionService;
    private final KeyboardService keyboardService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String callbackData = update.getCallbackQuery().getData();
        return callbackData != null && callbackData.startsWith(LANG_PREFIX);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String callbackData = update.getCallbackQuery().getData();

        messageTrackerService.deleteLastMessage(chatId);

        UserSession session = userSessionService.getOrCreate(chatId);

        String targetLocale = callbackData.replace(LANG_PREFIX, "");
        session.setLocale(targetLocale);

        userSessionService.save(session);

        String switchedMessage = localizationService.getLocalizedMessage(chatId, "language.switched");

        SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .replyMarkup(keyboardService.mainMenu(chatId)) // Regenerates localized reply options
                .text(switchedMessage)
                .build();

        eventPublisher.publishEvent(new MessageEvent(this, message));
    }

    @Override
    public String getCommand() {
        return CommandName.LANGUAGE.getCommandName();
    }
}