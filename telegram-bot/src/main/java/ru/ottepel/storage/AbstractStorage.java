package ru.ottepel.storage;

import ru.ottepel.model.User;

/**
 * Created by savetisyan on 17/03/17
 */
public interface AbstractStorage {
    void saveUser(User user);
    User getUser(long id);
}
