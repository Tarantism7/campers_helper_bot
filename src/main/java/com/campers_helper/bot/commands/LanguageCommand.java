package com.campers_helper.bot.commands;

import com.campers_helper.bot.events.MessageEvent;
import com.campers_helper.bot.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LanguageCommand implements Command {
    public static final String LANG_EN = "lang_en";
    public static final String LANG_DE = "lang_de";
    public static final String LANG_FR = "lang_fr";
    public static final String LANG_ES = "lang_es";
    public static final String LANG_IT = "lang_it";

    private final ApplicationEventPublisher eventPublisher;
    private final LocalizationService localizationService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        Long chatId = update.getMessage().getChatId();
        String localizedMessage = localizationService.getLocalizedMessage(chatId, "menu.language");
        return update.getMessage().getText().equals(localizedMessage);
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();

            String localizedMessage = localizationService.getLocalizedMessage(chatId, "language.select");

            SendMessage message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(localizedMessage)
                    .replyMarkup(languageInline(chatId))
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    private InlineKeyboardMarkup languageInline(Long chatId) {
        List<InlineKeyboardRow> rows = new ArrayList<>();

        rows.add(new InlineKeyboardRow(
                buildButton(chatId, "language.en", LANG_EN),
                buildButton(chatId, "language.de", LANG_DE)
        ));

        rows.add(new InlineKeyboardRow(
                buildButton(chatId, "language.es", LANG_ES),
                buildButton(chatId, "language.fr", LANG_FR)
        ));

        rows.add(new InlineKeyboardRow(
                buildButton(chatId, "language.it", LANG_IT)
        ));

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private InlineKeyboardButton buildButton(Long chatId, String propertyKey, String callbackData) {
        return InlineKeyboardButton.builder()
                .text(localizationService.getLocalizedMessage(chatId, propertyKey))
                .callbackData(callbackData)
                .build();
    }

    @Override
    public String getCommand() {
        return CommandName.LANGUAGE.getCommandName();
    }
}