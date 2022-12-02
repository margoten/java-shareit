package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item updateItem(Item item);

    Optional<Item> getItem(Integer id);

    Optional<Item> deleteItem(Integer id);

    List<Item> getItems();
}
