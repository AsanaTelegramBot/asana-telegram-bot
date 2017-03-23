package ru.youcon.ottepel.command;

import com.asana.models.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.youcon.ottepel.api.AsanaClient;
import ru.youcon.ottepel.model.TelegramUser;
import ru.youcon.ottepel.storage.AbstractStorage;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by savetisyan on 18/03/17
 */
public class WorkspacesListCommand extends BotCommand {
    private final AbstractStorage storage;
    private final AsanaClient asanaClient;

    @Autowired
    public WorkspacesListCommand(AbstractStorage storage, AsanaClient asanaClient) {
        super("workspaces", "List of workspaces in Asana");
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
            com.asana.models.User userInfo = tgUser.getUser();
            Collection<Workspace> workspaces = userInfo.workspaces;
            try {
                message.setText(createMessage(workspaces));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            sender.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String createMessage(Collection<Workspace> data) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Workspace workspace : data) {
            sb.append(workspace.name).append("\n");
        }
        return sb.toString();
    }
}
