package ru.youcon.ottepel.storage;

import org.springframework.stereotype.Component;
import ru.youcon.ottepel.model.TelegramUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class InMemoryStorage implements AbstractStorage {
    private Map<Long, TelegramUser> users = new HashMap<>();
    private Map<Long, TelegramUser> usersByChat = new HashMap<>();

    @Override
    public void saveUser(TelegramUser user) {
        users.put(user.getId(), user);
    }

    @Override
    public void saveUser(long chatId, TelegramUser user) {
        usersByChat.put(chatId, user);
    }

    @Override
    public TelegramUser getUser(long id) {
        return users.get(id);
    }

    @Override
    public TelegramUser getUserByChatId(long id) {
        return usersByChat.get(id);
    }
}
