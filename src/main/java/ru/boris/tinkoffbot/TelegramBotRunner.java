package ru.boris.tinkoffbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class TelegramBotRunner {
    public static void main(String[] args) {
        SpringApplication.run(TelegramBotRunner.class, args);
    }
}
