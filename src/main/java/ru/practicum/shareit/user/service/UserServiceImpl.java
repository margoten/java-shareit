package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public User createUser(User user) {
        validationUser(user);
        if (getUsers().stream().anyMatch(us -> us.getEmail().equals(user.getEmail()))) {
            log.warn("Некорректный адрес электронной почты {}.", user.getEmail());
            throw new ConflictException("Пользователь с таким email уже существует " + user.getEmail() + ".");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (getUsers().stream().anyMatch(us -> us.getEmail().equals(user.getEmail()))) {
            log.warn("Некорректный адрес электронной почты {}.", user.getEmail());
            throw new ConflictException("Пользователь с таким email уже существует " + user.getEmail() + ".");
        }
        User exist = getUser(user.getId());
        if (user.getEmail() != null) {
            exist.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            exist.setName(user.getName());
        }
        return userRepository.save(exist);
    }

    @Override
    public User getUser(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя не может быть пустым.");
        }
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        });
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя не может быть пустым.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll(Sort.sort(Integer.class));
    }

    private void validationUser(User user) {
        if (user.getEmail() == null) {
            log.warn("Email не может быть пустым.");
            throw new ValidationException("email не может быть пустым.");
        }

        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Некорректный адрес электронной почты {}.", user.getEmail());
            throw new ValidationException("Некорректный адрес электронной почты " + user.getEmail() + ".");
        }
    }
}
