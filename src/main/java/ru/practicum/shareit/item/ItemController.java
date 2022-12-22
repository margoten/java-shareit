package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
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
    private final BookingService bookingService;

    @GetMapping()
    public List<ItemExtendedDto> getAllItems(@RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.getItems(userId)
                .stream()
                .map(item -> ItemMapper.toItemExtendedDto(item,
                        bookingService.getLastItemBooking(item, userId),
                        bookingService.getNextItemBooking(item, userId),
                        itemService.getComments(item.getId())))
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.searchItems(text, userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto itemDto, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return ItemMapper.toItemDto(itemService.createItem(ItemMapper.toItem(itemDto), userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @PathVariable int itemId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemService.updateItem(item, userId));
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto getItem(@PathVariable int itemId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        Item item = itemService.getItem(itemId);
        return ItemMapper.toItemExtendedDto(item,
                bookingService.getLastItemBooking(item, userId),
                bookingService.getNextItemBooking(item, userId),
                itemService.getComments(item.getId()));
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createItemComment(@RequestBody CommentDto commentDto, @PathVariable int itemId, @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return CommentMapper.toCommentDto(itemService.createComment(CommentMapper.toComment(commentDto), itemId, userId));
    }
}
