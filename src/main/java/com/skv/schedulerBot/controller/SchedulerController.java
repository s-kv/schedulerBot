package com.skv.schedulerBot.controller;

import com.skv.schedulerBot.persistance.ScheduleRepository;
import com.skv.schedulerBot.persistance.WorkerRepository;
import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;

@BotController
public class SchedulerController {

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

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
