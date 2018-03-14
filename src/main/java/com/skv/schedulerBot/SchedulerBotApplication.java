package com.skv.schedulerBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@ComponentScan({"com.skv.telegram", "com.skv.schedulerBot"})
public class SchedulerBotApplication {
	public static void main(String[] args) {
		// Initialize Api Context
		ApiContextInitializer.init();
		SpringApplication.run(SchedulerBotApplication.class, args);
	}
}
