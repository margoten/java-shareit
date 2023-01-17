package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping()
    public List<ItemRequestDto> getItemRequests(@RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(@RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size,
                                                   @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getAllItemRequests(from, size, userId);
    }

    @PostMapping()
    public ItemRequestDto createItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                            @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemRequestService.createItemRequest(itemRequestDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@PathVariable int requestId,
                                         @RequestHeader(required = false, value = "X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequest(requestId, userId);
    }

}
