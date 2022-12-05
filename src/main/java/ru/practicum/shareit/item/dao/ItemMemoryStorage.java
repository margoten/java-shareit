package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemMemoryStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        Item exist = items.get(item.getId());
        if (item.getName() != null) {
            exist.setName(item.getName());
        }
        if (item.getDescription() != null) {
            exist.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            exist.setAvailable(item.getAvailable());
        }
        return exist;
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

}
