package com.skv.schedulerBot;

import com.skv.schedulerBot.fileHandler.FileHandler;
import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

@BotController
public class SchedulerBot {

    @Autowired
    FileHandler schedulerXlsHandler;

    @BotRequestMapping(value = "/ok")
    public SendMessage ok(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("okay bro, okay!");
    }

    @BotRequestMapping(value = BotRequestMapping.WRONG_MESSAGE)
    public SendMessage wrongMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("The message is wrong");
    }
}
