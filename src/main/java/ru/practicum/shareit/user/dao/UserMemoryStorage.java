package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class UserMemoryStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User exist = users.get(user.getId());
        if (user.getEmail() != null) {
            exist.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            exist.setName(user.getName());
        }
        return exist;
    }

    @Override
    public Optional<User> getUser(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> deleteUser(Integer id) {
        return Optional.ofNullable(users.remove(id));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
