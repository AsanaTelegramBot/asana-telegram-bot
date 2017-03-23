package ru.youcon.ottepel.storage;

import ru.youcon.ottepel.model.TelegramUser;

/**
 * Created by savetisyan on 17/03/17
 */
public interface AbstractStorage {
    void saveUser(TelegramUser user);
    void saveUser(long chatId, TelegramUser user);
    TelegramUser getUser(long id);
    TelegramUser getUserByChatId(long id);
}
