package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemController {
    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @GetMapping()
    public List<ItemExtendedDto> getAllItems(@RequestParam(required = false) Integer from,
                                             @RequestParam(required = false) Integer size,
                                             @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(required = false) String text,
                                     @RequestParam(required = false) Integer from,
                                     @RequestParam(required = false) Integer size,
                                     @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.searchItems(text, userId, from, size);
    }

    @PostMapping()
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        ItemRequestDto itemRequestDto = itemDto.getRequestId() == null ? null : itemRequestService.getItemRequest(itemDto.getRequestId(), userId);
        return itemService.createItem(itemDto, itemRequestDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable int itemId,
                              @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemExtendedDto getItem(@PathVariable int itemId,
                                   @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.getItem(itemId, userId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable int itemId) {
        itemService.deleteItem(itemId);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createItemComment(@RequestBody CommentDto commentDto,
                                        @PathVariable int itemId,
                                        @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemService.createComment(commentDto, itemId, userId);
    }
}
