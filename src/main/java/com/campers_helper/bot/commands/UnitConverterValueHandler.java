package com.campers_helper.bot.commands;

import com.campers_helper.bot.events.MessageEvent;
import com.campers_helper.bot.model.UserSession;
import com.campers_helper.bot.service.ConversionService;
import com.campers_helper.bot.service.LocalizationService;
import com.campers_helper.bot.service.UserSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UnitConverterValueHandler implements Command {

    private final UserSessionService sessionService;
    private final LocalizationService localizationService;
    private final ApplicationEventPublisher publisher;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;
        Long chatId = update.getMessage().getChatId();
        UserSession session = sessionService.getOrCreate(chatId);

        return session.getConverterState() == ConverterState.ENTER_VALUE;
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        UserSession session = sessionService.getOrCreate(chatId);

        double input;
        try {
            input = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            sendMessage(chatId, localizationService.getLocalizedMessage(chatId, "unit.invalid_number"));
            return;
        }

        if (session.getPrimaryValue() == null) {
            session.setPrimaryValue(input);
            sessionService.save(session);

            String missingUnitKey = determineMissingUnitKey(session.getSourceUnit(), session.getTargetUnit());
            String localizedMissingUnit = localizationService.getLocalizedMessage(chatId, missingUnitKey);

            sendMessage(chatId, localizationService.getLocalizedMessage(chatId, "unit.need_context", localizedMissingUnit));
            return;
        }

        double primaryValue = session.getPrimaryValue();
        UnitType source = session.getSourceUnit();
        UnitType target = session.getTargetUnit();

        double result = ConversionService.convert(source, target, primaryValue, input);

        String response = String.format(
                "✅ *%s*:\n%.2f %s = *%.2f %s*",
                localizationService.getLocalizedMessage(chatId, "unit.result_title"),
                primaryValue, source.name(), result, target.name()
        );

        session.setConverterState(ConverterState.CHOOSE_SOURCE);
        session.setPrimaryValue(null);
        sessionService.save(session);

        sendMessage(chatId, response);
    }

    private String determineMissingUnitKey(UnitType from, UnitType to) {
        if ((from == UnitType.WATT && to == UnitType.AMPERE) || (from == UnitType.AMPERE && to == UnitType.WATT)) {
            return "unit.volt";
        }
        if ((from == UnitType.WATT && to == UnitType.VOLT) || (from == UnitType.VOLT && to == UnitType.WATT)) {
            return "unit.ampere";
        }
        return "unit.watt";
    }

    private void sendMessage(Long chatId, String text) {
        publisher.publishEvent(new MessageEvent(this, SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("Markdown")
                .build()));
    }

    @Override
    public String getCommand() {
        return "converter_value";
    }
}