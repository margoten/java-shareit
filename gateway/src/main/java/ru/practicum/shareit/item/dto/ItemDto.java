package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotEmpty(message = "Item name cannot be empty")
    private String name;
    @NotEmpty(message = "Item description cannot be empty")
    private String description;
    @NotNull(message = "Item available cannot be empty")
    private Boolean available;
    private Integer ownerId;
    private Integer requestId;
}
