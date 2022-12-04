package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserDto {
    private final int id;
    private final String name;
    private final String email;
}
