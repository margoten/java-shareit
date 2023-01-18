package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "harry", "mail@mail.ru");
        user = UserMapper.toUser(userDto);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto returned = userService.createUser(userDto);
        Assertions.assertEquals(returned.getId(), user.getId());
        Assertions.assertEquals(returned.getEmail(), user.getEmail());
        Assertions.assertEquals(returned.getName(), user.getName());
    }

    @Test
    void createUserWhenEmailOtherUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(ConflictException.class);
        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
    }

    @Test
    void updateUser() {
        UserDto updated = new UserDto(1, "Updated", "updated@mail.ru");
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        userService.createUser(userDto);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.toUser(updated));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(UserMapper.toUser(updated)));
        UserDto returned = userService.updateUser(updated, updated.getId());
        Assertions.assertEquals(returned.getId(), updated.getId());
        Assertions.assertEquals(returned.getEmail(), updated.getEmail());
        Assertions.assertEquals(returned.getName(), updated.getName());
    }

    @Test
    void updateUserNameOnly() {
        UserDto update = new UserDto(1, "Updated", null);
        UserDto updated = new UserDto(1, "Updated", userDto.getEmail());
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        userService.createUser(userDto);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.toUser(updated));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(UserMapper.toUser(updated)));
        UserDto returned = userService.updateUser(update, updated.getId());
        Assertions.assertEquals(returned.getId(), updated.getId());
        Assertions.assertNotEquals(returned.getEmail(), update.getEmail());
        Assertions.assertEquals(returned.getName(), updated.getName());
    }

    @Test
    void updateUserEmailOnly() {
        UserDto update = new UserDto(1, null, "asa@sasa.ru");
        UserDto updated = new UserDto(1, userDto.getName(), "asa@sasa.ru");
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        userService.createUser(userDto);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(UserMapper.toUser(updated));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(UserMapper.toUser(updated)));
        UserDto returned = userService.updateUser(update, updated.getId());
        Assertions.assertEquals(returned.getId(), updated.getId());
        Assertions.assertEquals(returned.getEmail(), updated.getEmail());
        Assertions.assertNotEquals(returned.getName(), update.getName());
    }

    @Test
    void updateUserWhenEmailOtherUser() {
        UserDto update = new UserDto(2, "H", "mail1@mail.ru");

        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(Optional.ofNullable(UserMapper.toUser(update)));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenThrow(ConflictException.class);
        assertThrows(ConflictException.class, () -> userService.updateUser(userDto, 1));
    }

    @Test
    void updateUserWhenUserNull() {
        UserDto updated = new UserDto(1, "Updated", "updated@mail.ru");
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.updateUser(updated,null));
        Assertions.assertEquals("Id пользователя не может быть пустым.", ex.getMessage());
    }

    @Test
    void updateUserWhenUserNotFound() {
        UserDto updated = new UserDto(1, "Updated", "updated@mail.ru");
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.updateUser(updated, 99));
    }

    @Test
    void getUserWhenUserNotFound() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.getUser(99));
    }

    @Test
    void getUserWhenUserNull() {
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.getUser(null));
        Assertions.assertEquals("Id пользователя не может быть пустым.", ex.getMessage());
    }

    @Test
    void deleteUserWithNullId() {
        ValidationException ex = assertThrows(ValidationException.class, () -> userService.deleteUser(null));
        Assertions.assertEquals("Id пользователя не может быть пустым.", ex.getMessage());
    }

    @Test
    void deleteUser() {
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(user);
        UserDto returned = userService.createUser(userDto);
        userService.deleteUser(returned.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(user.getId());
    }

    @Test
    void getUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of(user));
        List<UserDto> returned = userService.getUsers();
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), user.getId());
    }

    @Test
    void getEmptyUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(List.of());
        List<UserDto> returned = userService.getUsers();
        Assertions.assertEquals(returned.size(), 0);
    }
}