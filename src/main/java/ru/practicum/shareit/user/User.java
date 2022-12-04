package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private int id;
    private String name;
    @Email
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
