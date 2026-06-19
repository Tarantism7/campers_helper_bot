package com.campers_helper.bot.commands;

import com.campers_helper.bot.events.MessageEvent;
import com.campers_helper.bot.model.UserSession;
import com.campers_helper.bot.service.LocalizationService;
import com.campers_helper.bot.service.MessageTrackerService;
import com.campers_helper.bot.service.UserSessionService;
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
public class UnitConverterCallbackCommand implements Command {
    private final ApplicationEventPublisher eventPublisher;
    private final LocalizationService localizationService;
    private final MessageTrackerService messageTrackerService;
    private final UserSessionService userSessionService;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasCallbackQuery()) return false;

        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        UserSession session = userSessionService.getOrCreate(chatId);
        ConverterState state = session.getConverterState();
        String data = update.getCallbackQuery().getData();

        return (state == ConverterState.CHOOSE_SOURCE || state == ConverterState.CHOOSE_TARGET) && isUnit(data);
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String data = update.getCallbackQuery().getData();

        UserSession session = userSessionService.getOrCreate(chatId);
        ConverterState state = session.getConverterState();
        UnitType unit = UnitType.fromCallback(data);
        messageTrackerService.deleteLastMessage(chatId);

        if (state == ConverterState.CHOOSE_SOURCE) {
            session.setSourceUnit(unit);
            session.setConverterState(ConverterState.CHOOSE_TARGET);
            userSessionService.save(session);

            SendMessage msg = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(localizationService.getLocalizedMessage(chatId, "unit.convert_to",
                            localizationService.getLocalizedMessage(chatId, unit.getMessageKey())))
                    .replyMarkup(buildTargetKeyboard(chatId, unit))
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, msg));
            return;
        }

        if (state == ConverterState.CHOOSE_TARGET) {
            session.setTargetUnit(unit);
            session.setConverterState(ConverterState.ENTER_VALUE);
            userSessionService.save(session);

            String localizedUnitName = localizationService.getLocalizedMessage(chatId, session.getSourceUnit().getMessageKey());
            String promptText = localizationService.getLocalizedMessage(chatId, "unit.enter_value", localizedUnitName);

            SendMessage msg = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(promptText)
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, msg));
        }
    }

    private InlineKeyboardMarkup buildTargetKeyboard(Long chatId, UnitType source) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow row = new InlineKeyboardRow();

        for (UnitType unit : UnitType.values()) {
            if (unit == source) continue;

            row.add(InlineKeyboardButton.builder()
                    .text(localizationService.getLocalizedMessage(chatId, unit.getMessageKey()))
                    .callbackData(unit.getCallbackData())
                    .build());

            if (row.size() == 2) {
                rows.add(row);
                row = new InlineKeyboardRow();
            }
        }

        if (!row.isEmpty()) rows.add(row);

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private boolean isUnit(String data) {
        try {
            UnitType.fromCallback(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getCommand() {
        return CommandName.CONVERT.getCommandName();
    }
}