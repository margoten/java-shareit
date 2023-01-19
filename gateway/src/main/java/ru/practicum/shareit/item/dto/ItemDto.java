package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.validation.Created;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(groups = Created.class, message = "Название товара не может быть пустым")
    private String name;
    @NotBlank(groups = Created.class, message = "Описание товара не может быть пустым")
    private String description;
    @NotNull(groups = Created.class, message = "Не задано состояние")
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
