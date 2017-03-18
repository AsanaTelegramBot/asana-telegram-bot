package ru.ottepel.api;

import com.asana.models.Event;
import com.asana.models.Project;
import com.asana.models.Story;
import com.asana.models.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.ottepel.api.config.EventList;
import ru.ottepel.api.util.json.EventDeserializer;
import ru.ottepel.bot.AsanaTelegramBot;
import ru.ottepel.model.TelegramUser;
import ru.ottepel.storage.InMemoryStorage;

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

    private String oldText;

    @RequestMapping(value = "/webhooks/{chatId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity webhooks(
            @RequestHeader(value = "X-Hook-Secret", required = false) String secret,
            @RequestHeader(value = "X-Hook-Signature", required = false) String signature,
            @PathVariable(value = "chatId") Long chatId,
            @RequestBody String data) {

        if (!"{}".equals(data)) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addDeserializer(Event.class, new EventDeserializer());
                mapper.registerModule(simpleModule);
                EventList eventList = mapper.readValue(data, EventList.class);
                TelegramUser user = storage.getUser(chatId);

                if (eventList.getEvents().length != 0) {
                    Event event = eventList.getEvents()[0];
                    String newText = "";
                    com.asana.models.User asanaUser = client.getUser(event.user.id, user.getToken());
                    switch (event.type) {
                        case "task":
                            Task task = client.getTask(event.resource.id, user.getToken());
                            newText = "Task " + task.name + " was " + event.action + " by " + asanaUser.name;
                            break;
                        case "project":
                            Project project = client.getProject(event.resource.id, user.getToken());
                            newText = "Project " + project.name + " was " + event.action + " by " + asanaUser.name;
                            break;
                        case "story":
                            Story story = client.getStory(event.resource.id, user.getToken());
                            newText = "Story " + story.type + " was " + event.action + " by " + asanaUser.name;
                            break;
                    }

                    if (!newText.equals(oldText)) {
                        bot.sendMessage(new SendMessage().setChatId(chatId).setText(newText));
                        oldText = newText;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Hook-Secret", secret);
        return new ResponseEntity(headers, HttpStatus.OK);
    }
}
