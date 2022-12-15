package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ConflictException("Пользователь с таким email уже существует " + user.getEmail() + ".");
            }
        }
        return null;
    }

    @Override
    public User updateUser(User user) {
        User exist = getUser(user.getId());
        if (user.getEmail() != null) {
            exist.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            exist.setName(user.getName());
        }
        try {
            return userRepository.save(exist);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ConflictException("Пользователь с таким email уже существует " + user.getEmail() + ".");
            }
        }
        return null;
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
        return userRepository.findAll();
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
