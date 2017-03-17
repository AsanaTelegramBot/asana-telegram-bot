package ru.ottepel.storage;

import org.springframework.stereotype.Component;
import ru.ottepel.model.TelegramUser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by savetisyan on 17/03/17
 */
@Component
public class InMemoryStorage implements AbstractStorage {
    private Map<Long, TelegramUser> users = new HashMap<>();

    @Override
    public void saveUser(TelegramUser user) {
        users.put(user.getId(), user);
    }

    @Override
    public TelegramUser getUser(long id) {
        return users.get(id);
    }
}
