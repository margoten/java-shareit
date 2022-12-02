package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    Item createItem(Item item);

    Item updateItem(Item item);


    Item getItem(Integer id);

    Item deleteItem(Integer id);

    List<Item> getItems();

    List<Item> searchItems(String text);
}
