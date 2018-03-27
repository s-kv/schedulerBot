package com.skv.telegram.handler;

import com.skv.telegram.api.BotRequestMapping;
import org.telegram.telegrambots.api.objects.Update;

public class BotRequestDispatcher {

    private static BotApiMethodContainer container = BotApiMethodContainer.getInstanse();

    public static BotApiMethodController getHandle(Update update) {
        String path;
        BotApiMethodController controller = null;

        if (update.hasMessage() && update.getMessage().hasText()) {
            path = update.getMessage().getText().split(" ")[0].trim();
            controller = container.getBotApiMethodController(path);
            if (controller == null)
                controller = container.getBotApiMethodController("");
        } else if (update.hasCallbackQuery()) {
            path = update.getCallbackQuery().getData().split("/")[0].trim();
            controller = container.getBotApiMethodController(path);
        } else if (update.hasMessage() && update.getMessage().hasDocument()) {
            String fileName = update.getMessage().getDocument().getFileName();
            if (fileName != null)
                controller = container.getBotApiMethodController(fileName.substring(fileName.indexOf('.', -1)));
        }

        return controller != null ? controller : container.getBotApiMethodController(BotRequestMapping.WRONG_MESSAGE);
    }
}
