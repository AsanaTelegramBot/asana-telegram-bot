package ru.ottepel.storage;

import ru.ottepel.model.TelegramUser;

/**
 * Created by savetisyan on 17/03/17
 */
public interface AbstractStorage {
    void saveUser(TelegramUser user);
    TelegramUser getUser(long id);
}
