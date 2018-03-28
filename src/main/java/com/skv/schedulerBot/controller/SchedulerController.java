package com.skv.schedulerBot.controller;

import com.skv.schedulerBot.domain.Worker;
import com.skv.schedulerBot.persistance.ScheduleRepository;
import com.skv.schedulerBot.persistance.WorkerRepository;
import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import com.skv.telegram.api.BotRequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@BotController
public class SchedulerController {

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @BotRequestMapping(value = "/get")
    public SendMessage get(Update update) {
        return getWorkerListMessage(update.getMessage().getChatId());
    }

    @BotRequestMapping(value = "workerList", messageType = BotRequestMethod.COMMAND)
    public SendMessage getWorkerListByButton(Update update) {
        return getWorkerListMessage(update.getCallbackQuery().getMessage().getChatId());
    }

    @BotRequestMapping(value = "get", messageType = BotRequestMethod.COMMAND)
    public SendMessage getWorkerSchedule(Update update) {
        long id = Long.parseLong(update.getCallbackQuery().getData().split("/")[1].trim());

        Worker worker = workerRepository.findById(id).get();

        StringBuilder message = new StringBuilder("Расписание пользователя " + worker.getFullName() + "\n");

        LocalDate futureDate = LocalDate.now().plusMonths(1);

        worker.getSchedule().stream()
                .filter(s -> s.getDate().isAfter(LocalDate.now()) && s.getDate().isBefore(futureDate))
                .forEach(s -> message.append(s + "\n"));

        return new SendMessage()
                .setChatId(update.getCallbackQuery().getMessage().getChatId())
                .setText(message.toString())
                .setReplyMarkup(getScheduleKeyboard("Назад к выбору пользователя"));
    }

    @BotRequestMapping(value = "/reg")
    public SendMessage registry(Update update) {
        long chatId = update.getMessage().getChatId();

        if (workerRepository.findByChatId(chatId) != null)
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Вы уже зарегистрированы!");
        else
            return new SendMessage()
                    .setChatId(update.getMessage().getChatId())
                    .setText("Кто вы?")
                    .setReplyMarkup(getWorkerKeyboard("reg", w -> w.getChatId() == 0));
    }

    @BotRequestMapping(value = "reg", messageType = BotRequestMethod.COMMAND)
    public SendMessage regWorker(Update update) {
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        if (workerRepository.findByChatId(chatId) != null)
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Вы уже зарегистрированы!");
        else {
            long id = Long.parseLong(update.getCallbackQuery().getData().split("/")[1].trim());

            Worker worker = workerRepository.findById(id).get();
            worker.setChatId(chatId);
            workerRepository.save(worker);

            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Добро пожаловать, " + worker.getFullName() + "!")
                    .setReplyMarkup(getScheduleKeyboard("Получить расписание"));
        }
    }

    @BotRequestMapping(value = "/unreg")
    public SendMessage unregistry(Update update) {
        long chatId = update.getMessage().getChatId();

        Worker worker = workerRepository.findByChatId(chatId);
        if (worker != null) {
            worker.setChatId(0);
            workerRepository.save(worker);

            return new SendMessage()
                    .setChatId(chatId)
                    .setText("До свидания, " + worker.getFullName() + "!");
        } else {
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Вы не зарегистрированы. В доступе отказано");
        }
    }

    @BotRequestMapping(value = BotRequestMapping.WRONG_MESSAGE)
    public SendMessage wrongMessage(Update update) {
        return new SendMessage()
                .setChatId(update.getMessage().getChatId())
                .setText("Команда не поддерживается." + "\n" +
                        "Для регистрации используйте команду /***" + "\n" +
                        "Используйте команду /get для получения расписания пользователей" + "\n" +
                        "или воспользуйтесь кнопкой ниже")
                .setReplyMarkup(getScheduleKeyboard("Получить расписание"));
    }

    private SendMessage getWorkerListMessage(long chatId) {
        if (workerRepository.findByChatId(chatId) != null)
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Выберите сотрудника:")
                    .setReplyMarkup(getWorkerKeyboard("get", w -> true));
        else
            return new SendMessage()
                    .setChatId(chatId)
                    .setText("Вы не зарегистрированы. В доступе отказано");
    }

    private InlineKeyboardMarkup getScheduleKeyboard(String callbackText) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> buttonRow = new ArrayList<>();
        buttonRow.add(new InlineKeyboardButton().setText(callbackText)
                .setCallbackData("workerList"));

        buttons.add(buttonRow);

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        return markupKeyboard;
    }

    private InlineKeyboardMarkup getWorkerKeyboard(String callbackPrefix, Predicate<Worker> check) {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        List<Worker> workerList = workerRepository.findAll(new Sort(Sort.Direction.ASC, "id"));

        for (Worker w : workerList.stream().filter(check).collect(Collectors.toList())) {
            List<InlineKeyboardButton> buttonRow = new ArrayList<>();
            buttonRow.add(new InlineKeyboardButton().setText(w.getFullName())
                    .setCallbackData(callbackPrefix + "/" + w.getId()));
            buttons.add(buttonRow);
        }

        InlineKeyboardMarkup markupKeyboard = new InlineKeyboardMarkup();
        markupKeyboard.setKeyboard(buttons);

        return markupKeyboard;
    }
}
