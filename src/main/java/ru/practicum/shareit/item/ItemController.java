package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.User;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;

    @GetMapping()
    public List<Item> getAllItems() {
        return itemService.getItems();
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam(required = false) String text) {
        return itemService.searchItems(text);
    }

    @PostMapping()
    public Item createItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") int userId) {
        return null;
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody Item item, @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.updateItem(item);
    }

    @GetMapping("/{itemId}")
    public Item getItem(@PathVariable int itemId) {
        return itemService.getItem(itemId);
    }

    @DeleteMapping("/{itemId}")
    public Item deleteItem(@PathVariable int itemId) {
        return itemService.deleteItem(itemId);
    }
}
