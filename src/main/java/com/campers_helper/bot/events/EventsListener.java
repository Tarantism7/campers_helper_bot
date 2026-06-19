package com.campers_helper.bot.events;

import com.campers_helper.bot.service.MessageTrackerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class EventsListener {
    private final TelegramClient telegramClient;
    private final MessageTrackerService messageTrackerService;

    @EventListener
    public void on(MessageEvent event) throws TelegramApiException {
        BotApiMethod<? extends Serializable> message = event.getMessage();
        Object sentMessage = telegramClient.execute(message);
        if (sentMessage instanceof Message && ((Message) sentMessage).getMessageId() != null) {
            messageTrackerService.saveLastMessage(
                    ((Message) sentMessage).getChatId(),
                    ((Message) sentMessage).getMessageId()
            );
        }
    }
}
