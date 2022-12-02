package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private final String name;
    private final String description;
    private final boolean available;
    private final Integer ownerId;
    private final Integer requestId;
}
