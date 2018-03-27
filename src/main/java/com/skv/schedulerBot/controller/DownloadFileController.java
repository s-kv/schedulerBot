package com.skv.schedulerBot.controller;

import com.skv.schedulerBot.domain.Schedule;
import com.skv.schedulerBot.domain.Worker;
import com.skv.schedulerBot.persistance.ScheduleRepository;
import com.skv.schedulerBot.persistance.WorkerRepository;
import com.skv.telegram.TelegramBot;
import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import com.skv.telegram.api.BotRequestMethod;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@BotController
public class DownloadFileController{
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @Autowired
    TelegramBot schedulerBot;

    @Autowired
    WorkerRepository workerRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @BotRequestMapping(value = ".xlsx", messageType = BotRequestMethod.FILE)
    public List<SendMessage> loadSchedule(Update update) {
        SendMessage response = new SendMessage()
                .setChatId(update.getMessage().getChatId());

        try {
            String fileId = update.getMessage().getDocument().getFileId();

            File file = schedulerBot.execute(new GetFile().setFileId(fileId));
            List<String> warnings = parseXls(schedulerBot.downloadFile(file));

            if (warnings.size() > 0)
                response.setText("Расписание загружено c предупреждениями:\n" + String.join("\n", warnings));
            else
                response.setText("Расписание загружено");
        } catch (IOException | TelegramApiException e) {
            response.setText("Ошибка при загрузке расписания: " + e.getMessage());
            e.printStackTrace();
        }

        List<SendMessage> responseList = alertWorkers();
        responseList.add(response);

        return responseList;
    }

    public List<SendMessage> alertWorkers() {
        List<SendMessage> alertList = new ArrayList<>();
        List<Worker> workerList = workerRepository.findAll();

        alertList.addAll(
                workerList.stream()
                        .filter(wrk -> wrk.getChatId() > 0)
                        .collect(Collectors.toList()).stream()
                        .map(w -> new SendMessage().setChatId(w.getChatId()).setText("Внимание! Расписание изменено!"))
                        .collect(Collectors.toList()));

        return alertList;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private List<String> parseXls(java.io.File xlsx) throws IOException {
        List<String> warnings = new ArrayList<>();
        InputStream inputStream = null;
        XSSFWorkbook workBook = null;

        try {
            inputStream = new FileInputStream(xlsx);
            workBook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //разбираем первый лист входного файла на объектную модель
        XSSFSheet sheet = workBook.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();

        List<List<String>> xlsData = new ArrayList<>();

        //проходим по всему листу
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();

            List<String> list = new ArrayList<>();
            int i = 0;

            while (cells.hasNext()) {
                Cell cell = cells.next();

                if (cell.getCellTypeEnum() == CellType.STRING)
                    list.add(cell.getStringCellValue());
                else if (cell.getCellTypeEnum() == CellType.NUMERIC || cell.getCellTypeEnum() == CellType.FORMULA)
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        if (i == 1)
                            list.add(new SimpleDateFormat(Schedule.DD_MM_YYYY).format(cell.getDateCellValue()));
                        else
                            list.add(new SimpleDateFormat(Schedule.HH_MM).format(cell.getDateCellValue()));
                    } else
                        list.add(String.valueOf(cell.getNumericCellValue()));
                else
                    list.add("");

                i++;
            }

            xlsData.add(list);
        }

        List<String> fullNames = xlsData.get(0);

        for (int i = 0; i < fullNames.size(); i++) {
            String fullName = fullNames.get(i);
            if (i > 2 && !fullName.equals("")) {
                Worker worker = workerRepository.findByFullName(fullName);

                if (worker == null) {
                    worker = new Worker();
                    worker.setFullName(fullName);
                }

                for (int j = 2; j < xlsData.size(); j ++) {
                    List<String> row = xlsData.get(j);

                    try {
                        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Schedule.DD_MM_YYYY);
                        LocalDate date = LocalDate.parse(row.get(1).trim(), dateFormatter);

                        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Schedule.HH_MM);
                        LocalTime startTime = null;
                        LocalTime endTime = null;
                        try {
                            if (!row.get(i).replaceAll("[-OО]", "").trim().equals(""))
                                startTime = LocalTime.parse(row.get(i).replace("-", "").trim(), timeFormatter);
                            if (!row.get(i+1).replaceAll("[-OО]", "").trim().equals(""))
                                endTime = LocalTime.parse(row.get(i+1).replace("-", "").trim(), timeFormatter);
                        } catch (DateTimeParseException e) {
                            String warning = "Ошибка при разборе формата времени (пользователь = " + fullName
                                    + ", дата = " + dateFormatter.format(date) + ")";
                            logger.info(warning);
                            warnings.add(warning);
                        }

                        Schedule sch = worker.getSchedule().stream().filter(o -> o.getDate().equals(date))
                                .findFirst().orElse(new Schedule(worker, date));

                        sch.setStartTime(startTime);
                        sch.setEndTime(endTime);

                        worker.getSchedule().add(sch);
                    } catch (DateTimeParseException e) {
                        String warning = "Ошибка при разборе формата даты (пользователь = " + fullName + ")";
                        logger.info(warning);
                        warnings.add(warning);
                    }
                }

                workerRepository.save(worker);
            }
        }

        return warnings;
    }
}
