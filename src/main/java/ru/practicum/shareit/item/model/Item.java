package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private int id;
    private final String name;
    private final String description;
    private final Boolean available;
    private User owner;
    private ItemRequest request;
}
