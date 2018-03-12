package com.skv.telegram.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

class BotApiMethodContainer {
    private static final Logger LOGGER = LoggerFactory.getLogger(BotApiMethodContainer.class);

    private Map<String, BotApiMethodController> controllerMap;

    public static BotApiMethodContainer getInstanse() {
        return Holder.INST;
    }

    public void addBotController(String path, BotApiMethodController controller) {
        if(controllerMap.containsKey(path))
            throw new BotApiMethodContainerException("path " + path + " already add");
        LOGGER.trace("add telegram bot controller for path: " +  path);
        controllerMap.put(path, controller);
    }

    public BotApiMethodController getBotApiMethodController(String path) {
        return controllerMap.get(path);
    }

    private BotApiMethodContainer() {
        controllerMap = new HashMap<>();
    }

    private static class Holder {
        final static BotApiMethodContainer INST = new BotApiMethodContainer();
    }
}
