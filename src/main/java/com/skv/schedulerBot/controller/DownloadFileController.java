package com.skv.schedulerBot.controller;

import com.skv.telegram.TelegramBot;
import com.skv.telegram.api.BotController;
import com.skv.telegram.api.BotRequestMapping;
import com.skv.telegram.api.BotRequestMethod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

@BotController
public class DownloadFileController{
    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @Autowired
    TelegramBot schedulerBot;

    @BotRequestMapping(value = ".xlsx", messageType = BotRequestMethod.FILE)
    public SendMessage loadSchedule(Update update) {
        SendMessage response = new SendMessage()
                .setChatId(update.getMessage().getChatId());

        try {
            String fileId = update.getMessage().getDocument().getFileId();

            File file = schedulerBot.execute(new GetFile().setFileId(fileId));
            parseXls(schedulerBot.downloadFile(file));

            response.setText("File processed");
        } catch (IOException | TelegramApiException e) {
            response.setText("File processed with exception: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    private void parseXls(java.io.File xlsx) throws IOException {
        String result = "";
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
        //проходим по всему листу
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            while (cells.hasNext()) {
                Cell cell = cells.next();
                int cellType = cell.getCellType();
                //перебираем возможные типы ячеек
                switch (cellType) {
                    case Cell.CELL_TYPE_STRING:
                        result += cell.getStringCellValue() + "=";
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        result += "[" + cell.getNumericCellValue() + "]";
                        break;

                    case Cell.CELL_TYPE_FORMULA:
                        result += "[" + cell.getNumericCellValue() + "]";
                        break;
                    default:
                        result += "|";
                        break;
                }
            }
            result += "\n";
        }
        logger.info(result);
    }
}
