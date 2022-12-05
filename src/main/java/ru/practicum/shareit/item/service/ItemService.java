package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item, Integer userId);

    Item updateItem(Item item, Integer userId);

    Item getItem(Integer id);

    void deleteItem(Integer id);

    List<Item> getItems(Integer ownerId);

    List<Item> searchItems(String text, Integer ownerId);
}
