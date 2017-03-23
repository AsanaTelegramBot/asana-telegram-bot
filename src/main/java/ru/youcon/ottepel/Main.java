package ru.youcon.ottepel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

@SpringBootApplication(scanBasePackages = "ru.youcon.ottepel")
public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {
        SpringApplication.run(Main.class, args);
    }
}