package com.skv.schedulerBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SchedulerBotApplication {
	private static final Logger logger = LoggerFactory.getLogger(SchedulerBotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SchedulerBotApplication.class, args);
	}
}
