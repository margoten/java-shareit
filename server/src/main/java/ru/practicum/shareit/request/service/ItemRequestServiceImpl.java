package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;


    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Integer requestorId) {
        User user = userRepository.findById(requestorId)
                .orElseThrow(() -> new NotFoundException("Not found user with id = " + requestorId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestByRequestorOrderByCreatedDesc(user);
        Map<Integer, List<ItemDto>> items = itemRepository.findAllByRequest_IdIn(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(int requestId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with id = " + userId));

        List<ItemDto> items = itemRepository.findAllByRequest_IdIs(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new NotFoundException("Запроса с id = " + requestId + " не существует."));
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Integer userId) {
        Pageable pageable = PaginationUtils.createPageRequest(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestByRequestor_IdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .collect(Collectors.toList());

        Map<Integer, List<ItemDto>> items = itemRepository.findAllByRequest_IdIn(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }
}
