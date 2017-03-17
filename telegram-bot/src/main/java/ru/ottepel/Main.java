package ru.ottepel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {
        SpringApplication.run(Main.class, args);
    }
}