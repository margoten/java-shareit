package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private int idGenerator = 0;

    @Override
    public Item createItem(Item item) {
        item.setId(generatedId());
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        return itemStorage.updateItem(item);
    }

    @Override
    public Item getItem(Integer id) {
        return itemStorage.getItem(id).orElseThrow(() -> {
            log.error("Товара с id = " + id + " не существует.");
            return null;
        });
    }

    @Override
    public Item deleteItem(Integer id) {
        return itemStorage.deleteItem(id).orElseThrow(() -> {
            log.error("Товара с id = " + id + " не существует.");
            return null;
        });
    }

    @Override
    public List<Item> getItems() {
        return itemStorage.getItems();
    }

    @Override
    public List<Item> searchItems(String text) {
        return getItems()
                .stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase())
                        || item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }

    private int generatedId() {
        return ++idGenerator;
    }
}
