package ru.ottepel.command;

import com.asana.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Chat;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.AbsSender;
import org.telegram.telegrambots.bots.commands.BotCommand;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.api.AsanaClient;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.AbstractStorage;

import java.io.IOException;
import java.util.List;

/**
 * Created by savetisyan on 18/03/17
 */
public class ProjectsListCommand extends BotCommand {
    private final AbstractStorage storage;
    private final AsanaClient asanaClient;

    @Autowired
    public ProjectsListCommand(AbstractStorage storage, AsanaClient asanaClient) {
        super("projects", "List of projects in workspace");
        this.storage = storage;
        this.asanaClient = asanaClient;
    }


    @Override
    public void execute(AbsSender sender, User user, Chat chat, String[] args) {
        TelegramUser tgUser = storage.getUser(user.getId());
        SendMessage message = new SendMessage().setChatId(chat.getId());
        try {
            if (args.length == 0) {
                message = message.setText("You haven't provided workspace name in the arguments...");
            } else {
                String command = args.length > 1 ? String.join(" ", args) : args[0];
                List<Project> projects = asanaClient.getProjects(command, tgUser.getToken());
                message.setText(createMessage(projects));
            }
        } catch (IOException e) {
            message = message.setText("Incorrect workspace name!");
        }

        try {
            sender.sendMessage(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String createMessage(List<Project> data) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Project project : data) {
            sb.append(project.name).append("\n");
        }
        return sb.toString();
    }
}
