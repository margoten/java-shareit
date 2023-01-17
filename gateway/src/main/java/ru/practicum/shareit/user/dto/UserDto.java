package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private final Integer id;
    @NotNull(message = "User name cannot be empty")
    private final String name;
    @Email(message = "User email is not valid")
    @NotEmpty(message = "User email cannot be empty")
    private final String email;
}
