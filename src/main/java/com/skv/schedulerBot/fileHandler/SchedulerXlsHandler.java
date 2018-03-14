package com.skv.schedulerBot.fileHandler;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SchedulerXlsHandler implements FileHandler {
    private final OkHttpClient client = new OkHttpClient();

    @Value("${botToken}")
    private String botToken;

    @Override
    public void process(String filePath) {
        Request request = new Request.Builder()
                .url("https://api.telegram.org/file/bot" + botToken + "/" + filePath)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);

                parseXls(response.body());
            }
        });
    }

    private void parseXls(ResponseBody body) {

    }
}
