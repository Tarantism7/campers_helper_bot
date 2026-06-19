package com.campers_helper.bot.config;

import com.campers_helper.bot.Bot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class TelegramConfig {
    @Bean
    public TelegramClient telegramClient(Bot bot) {
        return new OkHttpTelegramClient(bot.getBotToken());
    }
}
