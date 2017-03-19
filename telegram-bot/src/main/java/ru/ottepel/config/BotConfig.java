package ru.ottepel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.ottepel.api.AsanaClient;
import ru.ottepel.api.config.AsanaConfig;
import ru.ottepel.bot.AsanaTelegramBot;
import ru.ottepel.command.*;
import ru.ottepel.storage.AbstractStorage;
import ru.ottepel.storage.InMemoryStorage;

import javax.annotation.PostConstruct;

/**
 * Created by savetisyan on 17/03/17
 */
@Import(AsanaConfig.class)
@Configuration
public class BotConfig {
    @Value("${telegram.botName}")
    private String botName;

    @Autowired
    private AsanaClient asanaClient;

    @Bean
    public TypeAheadSearch typeAheadSearch() {
        return new TypeAheadSearch(inMemoryStorage());
    }

    @Bean
    public AbstractStorage inMemoryStorage() {
        return new InMemoryStorage();
    }

    @Bean
    public AsanaTelegramBot asanaTelegramBot() {
        return new AsanaTelegramBot(inMemoryStorage(), asanaClient, typeAheadSearch());
    }

    @PostConstruct
    public void init() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        AsanaTelegramBot bot = asanaTelegramBot();

        bot.registerAll(
                new StartCommand(asanaClient),
                new WorkspacesListCommand(inMemoryStorage(), asanaClient),
                new ProjectsListCommand(inMemoryStorage(), asanaClient),
                new SubscribeCommand(inMemoryStorage(), asanaClient),
                new UnsubscribeCommand(inMemoryStorage(), asanaClient),
                new SubscribeAllCommand(inMemoryStorage(), asanaClient),
                new HelpCommand());
        try {
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}

