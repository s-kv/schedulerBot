package com.skv.schedulerBot;

import com.skv.schedulerBot.domain.Schedule;
import com.skv.schedulerBot.domain.Worker;
import com.skv.schedulerBot.persistance.WorkerRepository;
import com.skv.telegram.TelegramBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EnableScheduling
public class Alert {
    @Autowired
    TelegramBot schedulerBot;

    @Autowired
    WorkerRepository workerRepository;

    @Scheduled(cron="0 0 12 * * *", zone="Europe/Moscow")
    public void workDayComing() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Schedule.HH_MM);
        List<SendMessage> alertList = new ArrayList<>();

        alertList.addAll(
                workerRepository.findAll().stream()
                        .filter(wrk -> wrk.getChatId() > 0 && newTomorrowStartTime(wrk))
                        .map(w -> new SendMessage().setChatId(w.getChatId())
                                .setText("Завтра на работу к " +
                                        w.getScheduleByDay(LocalDate.now().plusDays(1))
                                                .getStartTime().format(timeFormatter)))
                        .collect(Collectors.toList()));

        alertList.forEach(x -> {
            try {
                schedulerBot.execute(x); // Sending our message object to user
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean newTomorrowStartTime(Worker wrk) {
        Schedule today = wrk.getScheduleByDay(LocalDate.now());
        Schedule tomorrow = wrk.getScheduleByDay(LocalDate.now().plusDays(1));

        if (today != null && tomorrow != null)
            if (tomorrow.getStartTime() != null
                    && (today.getStartTime() == null || !today.getStartTime().equals(tomorrow.getStartTime())))
                return true;

        return false;
    }
}
