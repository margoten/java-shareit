package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    User updateUser(User user);

    User getUser(Integer userId);

    void deleteUser(Integer userId);

    List<User> getUsers();
}
