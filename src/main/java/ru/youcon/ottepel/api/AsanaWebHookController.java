package ru.youcon.ottepel.api;

import com.asana.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import ru.youcon.ottepel.api.config.EventList;
import ru.youcon.ottepel.bot.AsanaTelegramBot;
import ru.youcon.ottepel.model.TelegramUser;
import ru.youcon.ottepel.storage.InMemoryStorage;

import java.io.IOException;

/**
 * Created by savetisyan on 18/03/17
 */
@RestController
public class AsanaWebHookController {

    @Autowired
    private AsanaTelegramBot bot;

    @Autowired
    private AsanaClient client;

    @Autowired
    private InMemoryStorage storage;

    @Autowired
    private ObjectMapper mapper;

    private String oldText;

    @RequestMapping(value = "/webhooks/{chatId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public ResponseEntity webhooks(
            @RequestHeader(value = "X-Hook-Secret", required = false) String secret,
            @RequestHeader(value = "X-Hook-Signature", required = false) String signature,
            @PathVariable(value = "chatId") Long chatId,
            @RequestBody String data) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Hook-Secret", secret);
        if ("{}".equals(data)) {
            return new ResponseEntity(headers, HttpStatus.OK);
        }

        try {
            EventList eventList = mapper.readValue(data, EventList.class);
            TelegramUser user = storage.getUserByChatId(chatId);

            if(user == null) {
                return new ResponseEntity(headers, HttpStatus.OK);
            }

            if (eventList.getEvents().length != 0) {
                Event event = eventList.getEvents()[0];
                User asanaUser = client.getUser(event.user.id, user.getToken());
                String newText = generateUpdateMessage(user, event, asanaUser);
                if (!newText.equals(oldText)) {
                    bot.sendMessage(new SendMessage()
                            .setParseMode("Markdown")
                            .setChatId(chatId)
                            .setText(newText));
                    oldText = newText;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity(headers, HttpStatus.OK);
    }

    private String generateUpdateMessage(TelegramUser user, Event event, User asanaUser) throws IOException {
        String message = "%s *%s* was _%s_ by *%s*";
        String storyMessage = "%s *%s* by *%s*";

        StringBuilder newText = new StringBuilder();
        switch (event.type) {
            case "task":
                if (event.action.equals("deleted")) {
                    newText.append(String.format(message, "Task", "", event.action, asanaUser.name));
                } else {
                    Task task = client.getTask(event.resource.id, user.getToken());
                    newText.append(String.format(message, "Task", task.name, event.action, asanaUser.name))
                            .append("\n")
                            .append("*Assignee:* ").append(task.assignee != null ? task.assignee.name : " Not assigned")
                            .append("\n")
                            .append("*Status:* ").append(task.assigneeStatus)
                            .append("\n")
                            .append("*Last modified:* ").append(task.modifiedAt);
                }
                break;
            case "project":
                if (event.action.equals("deleted")) {
                    newText.append(String.format(message, "Project", "", event.action, asanaUser.name));
                } else {
                    Project project = client.getProject(event.resource.id, user.getToken());
                    newText.append(String.format(message, "Project", project.name, event.action, asanaUser.name))
                            .append("\n")
                            .append("*Notes:* ").append(project.notes)
                            .append("\n")
                            .append("*Is public:* ").append(project.isPublic)
                            .append("\n")
                            .append("*Last modified:* ").append(project.modifiedAt);
                }

                break;
            case "story":
                if (event.action.equals("deleted")) {
                    newText.append(String.format(storyMessage, "Story", "", asanaUser.name));
                } else {
                    Story story = client.getStory(event.resource.id, user.getToken());
                    newText.append(String.format(storyMessage, "Story", story.text, asanaUser.name))
                            .append("\n")
                            .append("*Type:* ").append(story.type)
                            .append("\n")
                            .append("*Owner:* ").append(story.createdBy.name)
                            .append("\n")
                            .append("*Created at:* ").append(story.createdAt);
                }
                break;
        }
        return newText.toString();
    }
}
