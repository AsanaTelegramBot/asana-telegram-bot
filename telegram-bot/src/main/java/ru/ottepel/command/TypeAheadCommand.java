package ru.ottepel.command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class TypeAheadCommand extends BotCommand {
    public TypeAheadCommand() {
        super("typeahead", "The typeahead provides search for objects from a single workspace.");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            absSender.sendMessage(new SendMessage()
                    .setChatId(chat.getId())
                    .setText("typeahead"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}