package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private int idGenerator = 0;

    @Override
    public Item createItem(Item item, Integer userId) {
        validationItem(item);
        User owner = userService.getUser(userId);
        item.setOwner(owner);
        item.setId(generatedId());
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Item item, Integer userId) {

        return itemStorage.updateItem(item);
    }

    @Override
    public Item getItem(Integer id) {
        return itemStorage.getItem(id).orElseThrow(() ->
                new NotFoundException("Товара с id = " + id + " не существует."));
    }

    @Override
    public void deleteItem(Integer id) {
        itemStorage.deleteItem(id).orElseThrow(() ->
                new NotFoundException("Товара с id = " + id + " не существует."));
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

    private void validationItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Название не может быть пустым.");
            throw new ValidationException("Название не может быть пустым.");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.warn("Описание не может быть пустым.");
            throw new ValidationException("Описание не может быть пустым.");
        }

        if (item.getAvailable() == null) {
            log.warn("Поле доступности пустое.");
            throw new ValidationException("Поле доступности пустое.");
        }
    }
}
