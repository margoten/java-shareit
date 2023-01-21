package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager entityManager;
    private final UserService service;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = createUserDto("Harry", "mail@mail.ru");
    }

    private UserDto createUserDto(String name, String email) {
        return new UserDto(null, name, email);
    }

    private void insertUsers() {
        service.createUser(createUserDto("Eva", "eva@mail.ru"));
        service.createUser(createUserDto("Adam", "adam@mail.ru"));
    }

    @Test
    void createUser() {
        service.createUser(userDto);

        User user = entityManager.createQuery("Select u from User u where u.email = :email", User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        service.createUser(userDto);
        User user = entityManager.createQuery("Select u from User u where u.email = :email", User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto updated = createUserDto("HarryUpdated", "mailUpdated@mail.com");
        service.updateUser(updated, user.getId());

        User userUpdated = entityManager.createQuery("Select u from User u where u.id = :id", User.class)
                .setParameter("id", user.getId())
                .getSingleResult();

        assertThat(userUpdated.getId(), notNullValue());
        assertThat(userUpdated.getName(), equalTo(updated.getName()));
        assertThat(userUpdated.getEmail(), equalTo(updated.getEmail()));
    }

    @Test
    void getUser() {
        service.createUser(userDto);
        User user = entityManager.createQuery("Select u from User u where u.email = :email", User.class)
                .setParameter("email", userDto.getEmail())
                .getSingleResult();

        UserDto userDtoReturned = service.getUser(user.getId());

        assertThat(userDtoReturned.getId(), equalTo(user.getId()));
        assertThat(userDtoReturned.getName(), equalTo(user.getName()));
        assertThat(userDtoReturned.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void deleteUser() {
        insertUsers();
        List<User> users = entityManager.createQuery("Select u from User u", User.class).getResultList();

        assertThat(users.size(), equalTo(2));
        service.deleteUser(users.get(0).getId());
        List<User> userAfter = entityManager.createQuery("Select u from User u", User.class).getResultList();

        assertThat(userAfter.size(), equalTo(1));

    }

    @Test
    void getUsers() {
        insertUsers();
        List<User> users = entityManager.createQuery("Select u from User u", User.class).getResultList();
        assertThat(users.size(), equalTo(2));
    }
}