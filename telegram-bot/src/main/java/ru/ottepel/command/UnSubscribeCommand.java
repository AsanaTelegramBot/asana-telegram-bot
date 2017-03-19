package ru.ottepel.command;

import com.asana.models.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.api.AsanaClient;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by vovcyan on 18.03.17.
 */
public class UnSubscribeCommand extends BotCommand {
    private final AbstractStorage storage;
    private final AsanaClient asanaClient;

    @Autowired
    public UnSubscribeCommand(AbstractStorage storage, AsanaClient asanaClient) {
        super("unsubscribe", "Unsubscribe from project notifications");
        this.storage = storage;
        this.asanaClient = asanaClient;
    }

    @Override
    public void execute(AbsSender sender, User user, Chat chat, String[] args) {
        TelegramUser tgUser = storage.getUser(user.getId());
        SendMessage message = new SendMessage().setChatId(chat.getId());

        if (tgUser == null) {
            message.setText("You're not authorized...");
        } else {
            try {
                com.asana.models.User userInfo = tgUser.getUser();
                Collection<Workspace> workspaces = userInfo.workspaces;

                message.setText("Choose workspace");
                message.setReplyMarkup(new InlineKeyboardMarkup()
                        .setKeyboard(generateKeyboard(workspaces)));

                sender.sendMessage(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private List<List<InlineKeyboardButton>> generateKeyboard(Collection<Workspace> workspaces) {
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        for (Workspace workspace : workspaces) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(workspace.name);
            button.setCallbackData("webhookList " + workspace.id);
            keyboardRows.add(Collections.singletonList(button));
        }

        return keyboardRows;
    }
}