package com.campers_helper.bot.events;


import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;

import java.io.Serializable;

@Getter
public class MessageEvent extends ApplicationEvent {
    private final BotApiMethod<? extends Serializable> message;

    public MessageEvent(Object source, BotApiMethod<? extends Serializable> message) {
        super(source);
        this.message = message;
    }
}
