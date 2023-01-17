package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceUnitTest {
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    ItemRequestService itemRequestService;
    private UserDto userDto;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "harry", "mail@mail.ru");
        itemRequestDto = new ItemRequestDto(1, "Request", 1, LocalDateTime.now(), List.of());
        itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, userDto);
        itemRequestService = new ItemRequestServiceImpl(userService, itemService, itemRequestRepository);
    }

    ItemRequestDto createItemRequestDto() {
        Mockito.when(userService.getUser(any()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        return itemRequestService.createItemRequest(itemRequestDto, userDto.getId());
    }

    @Test
    void createItemRequest() {
        ItemRequestDto returned = createItemRequestDto();
        Assertions.assertEquals(returned.getId(), itemRequest.getId());
        Assertions.assertEquals(returned.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(returned.getRequestorId(), itemRequest.getRequestor().getId());
    }

    @Test
    void createItemWithEmptyDescription() {
        ValidationException ex = assertThrows(ValidationException.class, () -> itemRequestService.createItemRequest(new ItemRequestDto(1, "", 1, LocalDateTime.now(), List.of()), 1));
        Assertions.assertEquals("Запрос не может быть пустым", ex.getMessage());
    }

    @Test
    void getItemRequests() {
        createItemRequestDto();

        Mockito.when(itemRequestRepository.findItemRequestByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> returned = itemRequestService.getItemRequests(userDto.getId());
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), itemRequest.getId());
        Assertions.assertEquals(returned.get(0).getItems().size(), 0);
    }


    @Test
    void getItemRequestsWithItems() {
        createItemRequestDto();

        Mockito.when(itemService.getItemsByRequests(any()))
                .thenReturn(List.of(new ItemDto(1, "name", "desr", true, 3, 1)));
        Mockito.when(itemRequestRepository.findItemRequestByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of(itemRequest));
        List<ItemRequestDto> returned = itemRequestService.getItemRequests(userDto.getId());
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), itemRequest.getId());
        Assertions.assertEquals(returned.get(0).getItems().size(), 1);
    }

    @Test
    void getItemRequestsWithEmptyResult() {
        createItemRequestDto();

        Mockito.when(itemRequestRepository.findItemRequestByRequestorOrderByCreatedDesc(any()))
                .thenReturn(List.of());
        List<ItemRequestDto> returned = itemRequestService.getItemRequests(userDto.getId());
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void getItemRequest() {
        createItemRequestDto();


        Mockito.when(itemService.getItemsByRequestId(any()))
                .thenReturn(List.of(new ItemDto(1, "name", "desr", true, 3, 1)));
        Mockito.when(itemRequestRepository.findById(any()))
                .thenReturn(java.util.Optional.ofNullable(itemRequest));
        ItemRequestDto returned = itemRequestService.getItemRequest(2, userDto.getId());
        Assertions.assertEquals(returned.getDescription(), itemRequest.getDescription());
        Assertions.assertEquals(returned.getId(), itemRequest.getId());
        Assertions.assertEquals(returned.getItems().size(), 1);
    }

    @Test
    void getItemRequestNotFound() {
        Mockito.when(userService.getUser(any()))
                .thenReturn(userDto);
        Mockito.when(itemRequestRepository.findById(any()))
                .thenThrow(NotFoundException.class);
        Mockito.when(itemService.getItemsByRequestId(any()))
                .thenReturn(List.of());
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(99, 99));
    }

    @Test
    void getAllItemRequests() {
        createItemRequestDto();

        Mockito.when(itemService.getItemsByRequests(any()))
                .thenReturn(List.of(new ItemDto(1, "name", "desr", true, 3, 1)));
        Mockito.when(itemRequestRepository.findItemRequestByRequestor_IdIsNotOrderByCreatedDesc(any(), any()))
                .thenReturn(new PageImpl<>(List.of(itemRequest)));
        List<ItemRequestDto> returned = itemRequestService.getAllItemRequests(null, null, userDto.getId());
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), itemRequest.getId());
        Assertions.assertEquals(returned.get(0).getItems().size(), 1);
    }
}