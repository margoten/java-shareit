package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;

    @GetMapping()
    public List<ItemDto> getAllItems() {
        return itemService.getItems()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text) {
        return itemService.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto item, @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ItemMapper.toItemDto(itemService.createItem(ItemMapper.toItem(item), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto item, @PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int userId) {
        return null;
        //return itemService.updateItem(item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return ItemMapper.toItemDto(itemService.getItem(itemId));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }
}
