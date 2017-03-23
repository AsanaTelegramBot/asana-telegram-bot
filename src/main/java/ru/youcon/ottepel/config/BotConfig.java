package ru.youcon.ottepel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.youcon.ottepel.api.AsanaClient;
import ru.youcon.ottepel.api.config.AsanaConfig;
import ru.youcon.ottepel.bot.AsanaTelegramBot;
import ru.youcon.ottepel.command.*;
import ru.youcon.ottepel.storage.AbstractStorage;
import ru.youcon.ottepel.storage.InMemoryStorage;

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
        return new TypeAheadSearch(inMemoryStorage(), asanaClient);
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

