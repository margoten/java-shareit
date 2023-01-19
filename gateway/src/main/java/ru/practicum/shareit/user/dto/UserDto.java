package ru.practicum.shareit.user.dto;

import lombok.Data;
import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class UserDto {
    private Integer id;
    @NotBlank(groups = Created.class, message = "Имя товара не может быть пустым")
    private String name;
    @Email(groups = {Updated.class, Created.class}, message = "Email задан не корректно")
    @NotEmpty(groups = Created.class, message = "Email товара не может быть пустым")
    private String email;
}
