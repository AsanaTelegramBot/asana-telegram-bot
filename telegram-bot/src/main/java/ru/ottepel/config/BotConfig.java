package ru.ottepel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.ottepel.AsanaClient;
import ru.ottepel.AsanaTelegramBot;
import ru.ottepel.command.AuthCommand;
import ru.ottepel.storage.AbstractStorage;
import ru.ottepel.storage.InMemoryStorage;

import javax.annotation.PostConstruct;

/**
 * Created by savetisyan on 17/03/17
 */
@Import(AsanaConfig.class)
@Configuration
public class BotConfig {
    @Autowired
    private AsanaClient asanaClient;

    @Bean
    public AbstractStorage inMemoryStorage() {
        return new InMemoryStorage();
    }

    @Bean
    public AsanaTelegramBot asanaTelegramBot() {
        return new AsanaTelegramBot(inMemoryStorage(), asanaClient);
    }


    @PostConstruct
    public void init() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        AsanaTelegramBot bot = asanaTelegramBot();
        bot.registerAll(new AuthCommand(inMemoryStorage(), asanaClient));

        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
