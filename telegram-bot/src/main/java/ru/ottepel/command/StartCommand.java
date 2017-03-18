package ru.ottepel.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.AsanaClient;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class StartCommand extends BotCommand {
    private static final String messageTemplate = "Please, open the following link in the browser and send code to this chat.\n%s";
    private final AsanaClient asanaClient;

    @Autowired
    public StartCommand(AsanaClient asanaClient) {
        super("start", "Authorize in Asana");
        this.asanaClient = asanaClient;
    }

    @Override
    public void execute(AbsSender sender, User user, Chat chat, String[] strings) {
        try {
            String authLink = asanaClient.getAuthLink(user.getId());
            sender.sendMessage(new SendMessage()
                    .setText(String.format(messageTemplate, authLink))
                    .setChatId(chat.getId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}