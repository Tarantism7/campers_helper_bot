package com.campers_helper.bot.commands;

import com.campers_helper.bot.events.MessageEvent;
import com.campers_helper.bot.model.UserSession;
import com.campers_helper.bot.service.LocalizationService;
import com.campers_helper.bot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UnitConverterCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final LocalizationService localizationService;
    private final UserSessionService userSessionService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = update.getMessage().getChatId();
        String localizedMessage = localizationService.getLocalizedMessage(chatId, "menu.converter");
        return update.getMessage().getText().equals(localizedMessage);
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            UserSession session = userSessionService.getOrCreate(chatId);
            session.setConverterState(ConverterState.CHOOSE_SOURCE);
            userSessionService.save(session);

            String localizedMessage = localizationService.getLocalizedMessage(chatId, "unit.select");

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(localizedMessage)
                    .replyMarkup(buildKeyboard(chatId))
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    private InlineKeyboardMarkup buildKeyboard(Long chatId) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        new InlineKeyboardRow(
                                button(chatId, UnitType.WATT),
                                button(chatId, UnitType.AMPERE)
                        ),
                        new InlineKeyboardRow(
                                button(chatId, UnitType.VOLT)
                        )
                ))
                .build();
    }

    private InlineKeyboardButton button(Long chatId, UnitType unitType) {
        return InlineKeyboardButton.builder()
                .text(localizationService.getLocalizedMessage(chatId, unitType.getMessageKey()))
                .callbackData(unitType.getCallbackData())
                .build();
    }

    @Override
    public String getCommand() {
        return CommandName.CONVERT.getCommandName();
    }
}