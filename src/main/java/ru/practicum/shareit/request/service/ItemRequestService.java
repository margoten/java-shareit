package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Integer requestorId);

    List<ItemRequestDto> getItemRequests(Integer userId);

    ItemRequestDto getItemRequest(int requestId, Integer userId);

    List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Integer userId);
}
