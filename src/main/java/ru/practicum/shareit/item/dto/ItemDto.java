package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class ItemDto {
    private int id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final Integer ownerId;
    private final Integer requestId;
}
