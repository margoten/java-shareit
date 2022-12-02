package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
public class ItemMemoryStorage implements ItemStorage{
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer id;
    @Override
    public Item createItem(Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> getItem(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Optional<Item> deleteItem(Integer id) {
        return Optional.ofNullable(items.remove(id));
    }

    @Override
    public List<Item> getItems() {
        return new ArrayList<>(items.values());
    }

    private Integer generateId() {
        return ++id;
    }
}
