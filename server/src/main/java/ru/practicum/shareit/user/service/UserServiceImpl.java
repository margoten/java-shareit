package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
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
        return userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        });
    }
}
