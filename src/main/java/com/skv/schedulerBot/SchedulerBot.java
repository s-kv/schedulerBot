package com.skv.schedulerBot;

import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import com.skv.telegram.handler.BotRequestDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.List;

@BotController
public class SchedulerBot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerBot.class);

    @Value("${botUsername}")
    private String botUsername;

    @Value("${botToken}")
    private String botToken;

    @PostConstruct
    public void init() {
        logger.info("registration SchedulerBot...");
        //logger.info("botUsername = " + botUsername);
        //logger.info("botToken = " + botToken);

        // Initialize Api Context
        ApiContextInitializer.init();

        // Instantiate Telegram Bots API
        TelegramBotsApi botsApi = new TelegramBotsApi();

        // Register our bot
        try {
            botsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<BotApiMethod> responseList = BotRequestDispatcher.getHandle(update).process(update);

        for (BotApiMethod response : responseList) {
            try {
                execute(response); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @BotRequestMapping(value = "/ok")
    public SendMessage ok(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("okay bro, okay!");
    }
}
