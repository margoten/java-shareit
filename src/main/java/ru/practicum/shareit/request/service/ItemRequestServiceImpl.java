package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestRepository itemRequestRepository;
    private static final Pageable PAGEABLE_DEFAULT = PaginationUtils.createPageRequest(0, 100, Sort.by("created").descending());


    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Integer requestorId) {
        validationItemRequest(itemRequestDto);
        User user = UserMapper.toUser(userService.getUser(requestorId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Integer userId) {
        User user = UserMapper.toUser(userService.getUser(userId));
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestByRequestorOrderByCreatedDesc(user);
        Map<Integer, List<ItemDto>> items = itemService.getItemsByRequests(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequest(int requestId, Integer userId) {
        User user = UserMapper.toUser(userService.getUser(userId));

        List<ItemDto> items = itemService.getItemsByRequestId(requestId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new NotFoundException("Запроса с id = " + requestId + " не существует."));
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer from, Integer size, Integer userId) {
        Pageable pageable = from == null || size == null
                ? PAGEABLE_DEFAULT
                : PaginationUtils.createPageRequest(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findItemRequestByRequestor_IdIsNotOrderByCreatedDesc(userId, pageable)
                .stream()
                .collect(Collectors.toList());

        Map<Integer, List<ItemDto>> items = itemService.getItemsByRequests(itemRequests.stream().map(ItemRequest::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(ItemDto::getRequestId));
        return itemRequests
                .stream()
                .map(itemRequest -> ItemRequestMapper.toItemRequestDto(itemRequest, items.get(itemRequest.getId())))
                .collect(Collectors.toList());
    }

    private void validationItemRequest(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Запрос не может быть пустым");
        }
    }
}
