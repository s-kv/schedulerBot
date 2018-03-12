package com.skv.schedulerBot;

import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import com.skv.telegram.handler.BotRequestDispatcher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.List;

@Component
@BotController
public class SchedulerBot extends TelegramLongPollingBot {

    @BotRequestMapping(value = "/ok")
    public SendMessage ok(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("okay bro, okay!");
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
        // TODO
        return null;
    }

    @Override
    public String getBotToken() {
        // TODO
        return null;
    }
}
