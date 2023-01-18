package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    private Integer id;
    @NotBlank(groups = Created.class, message = "User name cannot be empty")
    private String name;
    @Email(groups = {Updated.class, Created.class}, message = "User email is not valid")
    @NotEmpty(groups = Created.class, message = "User email cannot be empty")
    private String email;
}
