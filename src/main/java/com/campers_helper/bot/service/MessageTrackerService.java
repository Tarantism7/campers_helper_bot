package com.campers_helper.bot.service;

import com.campers_helper.bot.events.MessageEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MessageTrackerService {
    private final Map<Long, Integer> lastBotMessages = new ConcurrentHashMap<>();
    private final ApplicationEventPublisher eventPublisher;

    public void saveLastMessage(Long chatId, Integer messageId){
        lastBotMessages.put(chatId, messageId);
    }

    public void deleteLastMessage(Long chatId){
        Integer lastMessageId = lastBotMessages.get(chatId);
        if(lastMessageId != null){
            DeleteMessage deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(lastMessageId)
                    .build();
            eventPublisher.publishEvent(new MessageEvent(this,  deleteMessage));
        }
    }
}
