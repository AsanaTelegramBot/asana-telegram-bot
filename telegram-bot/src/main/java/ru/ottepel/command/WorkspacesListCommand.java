package ru.ottepel.command;

import com.asana.models.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.AsanaClient;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

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
    public void execute(AbsSender sender, User user, Chat chat, String[] strings) {
        TelegramUser tgUser = storage.getUser(chat.getId());
        com.asana.models.User userInfo = tgUser.getUser();
        Collection<Workspace> workspaces = userInfo.workspaces;

        try {
            sender.sendMessage(new SendMessage()
                    .setText(createMessage(workspaces))
                    .setChatId(chat.getId()));
        } catch (TelegramApiException | IOException e) {
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
