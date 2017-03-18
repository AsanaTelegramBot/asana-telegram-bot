package ru.ottepel.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * Created by savetisyan on 18/03/17
 */
public class HelpCommand extends BotCommand {
    public HelpCommand() {
        super("help", "Show list of commands");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            absSender.sendMessage(new SendMessage()
                    .setChatId(chat.getId())
                    .setText("help"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
