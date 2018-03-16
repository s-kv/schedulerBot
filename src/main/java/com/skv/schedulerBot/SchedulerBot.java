package com.skv.schedulerBot;

import com.skv.telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SchedulerBot extends TelegramBot {
    @Value("${botUsername}")
    private String botUsername;

    @Value("${botToken}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
