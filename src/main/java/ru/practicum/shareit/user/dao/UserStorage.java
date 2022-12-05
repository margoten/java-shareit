package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    Optional<User> getUser(Integer id);

    Optional<User> deleteUser(Integer id);

    List<User> getUsers();
}
