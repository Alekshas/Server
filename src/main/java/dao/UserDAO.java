package dao;

import models.User;

public interface UserDAO {
    void save(User user);
    User findByLogin(String login);
    void banControlByLogin(String login, boolean value);
}
