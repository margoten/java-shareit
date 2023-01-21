package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private ItemDto itemDto;
    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User(1, "harry", "mail@mail.ru");
        item = new Item(1,
                "Item",
                "Description",
                true,
                user,
                null);
        itemDto = ItemMapper.toItemDto(item);
        itemService = new ItemServiceImpl(itemRepository, commentRepository, userRepository, bookingRepository, itemRequestRepository);
    }

    private ItemDto createItemDto() {
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(itemRepository.save(any()))
                .thenReturn(ItemMapper.toItem(itemDto));
        return itemService.createItem(itemDto, user.getId());
    }

    @Test
    void createItem() {
        ItemDto created = createItemDto();
        Assertions.assertEquals(created.getId(), item.getId());
        Assertions.assertEquals(created.getName(), item.getName());
    }

    @Test
    void updateItem() {
        ItemDto created = createItemDto();
        Item updated = new Item(created.getId(), "newName", itemDto.getDescription(), itemDto.getAvailable(), user, null);
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(itemRepository.save(any()))
                .thenReturn(updated);
        ItemDto returned = itemService.updateItem(ItemMapper.toItemDto(updated), user.getId());
        Assertions.assertEquals(created.getId(), returned.getId());
        Assertions.assertNotEquals(created.getName(), returned.getName());

    }

    @Test
    void getItemWithItemNotFound() {
        createItemDto();
        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.getItem(99, user.getId()));
    }

    @Test
    void getItems() {
        createItemDto();
        Mockito.when(itemRepository.findAllByOwner_IdIs(anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        Mockito.when(commentRepository.findCommentByItemInOrderByCreated(anyList()))
                .thenReturn(List.of());

        Mockito.when(bookingRepository.findBookingsByItemInOrderByStartDesc(any()))
                .thenReturn(List.of());

        List<ItemExtendedDto> returned = itemService.getItems(user.getId(), 0, 10);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), item.getId());


    }

    @Test
    void searchItems() {
        createItemDto();

        Mockito.when(itemRepository.search(anyString(), any()))
                .thenReturn(new PageImpl<>(List.of(item)));

        List<ItemDto> returned = itemService.searchItems("item", user.getId(), 0, 10);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), item.getId());
    }

    @Test
    void searchItemsWithEmptyResult() {
        createItemDto();

        Mockito.when(itemRepository.search(anyString(), any()))
                .thenReturn(Page.empty());

        List<ItemDto> returned = itemService.searchItems("Hello", user.getId(), 0, 10);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void searchItemsWithEmptyText() {
        List<ItemDto> returned = itemService.searchItems("", user.getId(), 0, 10);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void createComment() {
        ItemDto created = createItemDto();
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), user.getName(), LocalDateTime.now());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(bookingRepository.existsBookingByBookerIsAndEndBefore(any(), any()))
                .thenReturn(true);

        Mockito.when(commentRepository.save(any()))
                .thenReturn(new Comment(1, commentDto.getText(), item, user, LocalDateTime.now()));

        CommentDto returned = itemService.createComment(commentDto, item.getId(), 2);
        Assertions.assertEquals(returned.getId(), commentDto.getId());
        Assertions.assertEquals(returned.getText(), commentDto.getText());

    }

    @Test
    void createCommentWithoutBookings() {
        ItemDto created = createItemDto();
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), user.getName(), LocalDateTime.now());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(userRepository.findById(Mockito.any()))
                .thenReturn(java.util.Optional.ofNullable(user));
        Mockito.when(bookingRepository.existsBookingByBookerIsAndEndBefore(any(), any()))
                .thenReturn(false);

        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, item.getId(), 2));
    }

    @Test
    void createCommentToNotFoundItem() {
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), user.getName(), LocalDateTime.now());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, 99, 2));

    }
}