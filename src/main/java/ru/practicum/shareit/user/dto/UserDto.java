package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private final Integer id;
    private final String name;
    @Email
    private final String email;
}
