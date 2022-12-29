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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public UserDto createUser(UserDto userDto) {
        validationUser(userDto);
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ConflictException("Пользователь с таким email уже существует " + userDto.getEmail() + ".");
            }
        }
        return null;
    }

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User exist = getUserFromDB(userId);
        if (userDto.getEmail() != null) {
            exist.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            exist.setName(userDto.getName());
        }
        try {
            return UserMapper.toUserDto(userRepository.save(exist));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                throw new ConflictException("Пользователь с таким email уже существует " + userDto.getEmail() + ".");
            }
        }
        return null;
    }

    @Override
    public UserDto getUser(Integer userId) {
        return UserMapper.toUserDto(getUserFromDB(userId));
    }

    @Override
    public void deleteUser(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя не может быть пустым.");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User getUserFromDB(Integer userId) {
        if (userId == null) {
            throw new ValidationException("Id пользователя не может быть пустым.");
        }
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        });
    }


    private void validationUser(UserDto user) {
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
