package com.skv.schedulerBot.controller;

import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

@BotController
public class SchedulerController {

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
