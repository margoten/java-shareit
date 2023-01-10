package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.service.BookingService;
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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

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
    private UserService userService;
    @Mock
    private BookingService bookingService;
    private ItemService itemService;
    private ItemDto itemDto;
    private UserDto userDto;
    private Item item;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(1, "harry", "mail@mail.ru");
        item = new Item(1,
                "Item",
                "Description",
                true,
                UserMapper.toUser(userDto),
                null);
        itemDto = ItemMapper.toItemDto(item);
        itemService = new ItemServiceImpl(itemRepository, commentRepository, userService, bookingService);
    }

    private ItemDto createItemDto() {
        Mockito.when(userService.getUser(Mockito.any()))
                .thenReturn(userDto);
        Mockito.when(itemRepository.save(any()))
                .thenReturn(ItemMapper.toItem(itemDto));
        return itemService.createItem(itemDto, null, userDto.getId());
    }

    @Test
    void createItem() {
        ItemDto created = createItemDto();
        Assertions.assertEquals(created.getId(), item.getId());
        Assertions.assertEquals(created.getName(), item.getName());
    }

    @Test
    void createItemWithEmptyName() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createItem(new ItemDto(null, "", "11", true, 1, 1), null, 1));
        Assertions.assertEquals("Название не может быть пустым.", ex.getMessage());
    }

    @Test
    void createItemWithEmptyDescription() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createItem(new ItemDto(null, "11", "", true, 1, 1), null, 1));
        Assertions.assertEquals("Описание не может быть пустым.", ex.getMessage());
    }

    @Test
    void createItemWithNulAvailable() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createItem(new ItemDto(null, "11", "11", null, 1, 1), null, 1));
        Assertions.assertEquals("Поле доступности пустое.", ex.getMessage());
    }

    @Test
    void updateItem() {
        ItemDto created = createItemDto();
        Item updated = new Item(created.getId(), "newName", itemDto.getDescription(), itemDto.getAvailable(), UserMapper.toUser(userDto), null);
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(itemRepository.save(any()))
                .thenReturn(updated);
        ItemDto returned = itemService.updateItem(ItemMapper.toItemDto(updated), userDto.getId());
        Assertions.assertEquals(created.getId(), returned.getId());
        Assertions.assertNotEquals(created.getName(), returned.getName());

    }

    @Test
    void updateItemWithOwnerNull() {
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.updateItem(itemDto, null));
        Assertions.assertEquals("Не заполненное поле владельца", ex.getMessage());
    }

    @Test
    void getItemWithItemNotFound() {
        createItemDto();
        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.getItem(99, userDto.getId()));
    }

    @Test
    void getItems() {
        createItemDto();
        Mockito.when(commentRepository.findAll())
                .thenReturn(List.of());

        Mockito.when(bookingService.getOwnersBookings(anyInt(), any()))
                .thenReturn(List.of());

        Mockito.when(itemRepository.findAllByOwner_IdIs(anyInt()))
                .thenReturn(List.of(item));

        List<ItemExtendedDto> returned = itemService.getItems(userDto.getId(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), item.getId());


    }

    @Test
    void searchItems() {
        createItemDto();

        Mockito.when(itemRepository.search(anyString()))
                .thenReturn(List.of(item));

        List<ItemDto> returned = itemService.searchItems("item", userDto.getId(), null, null);
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), item.getId());
    }

    @Test
    void searchItemsWithEmptyResult() {
        createItemDto();

        Mockito.when(itemRepository.search(anyString()))
                .thenReturn(List.of());

        List<ItemDto> returned = itemService.searchItems("Hello", userDto.getId(), null, null);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void searchItemsWithEmptyText() {
        List<ItemDto> returned = itemService.searchItems("", userDto.getId(), null, null);
        Assertions.assertEquals(returned.size(), 0);
    }

    @Test
    void createComment() {
        ItemDto created = createItemDto();
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        BookingExtendedDto bookingExtendedDto = new BookingExtendedDto(1,
                LocalDateTime.of(2023, 1, 1, 0, 1, 1),
                LocalDateTime.of(2023, 1, 1, 1, 1, 1),
                new ItemDto(
                        1,
                        "Item1",
                        "Description1",
                        true, 1, 1),
                new UserDto(
                        1,
                        "Harry",
                        "mail@mail.com"),
                Booking.BookingState.WAITING.name());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(userService.getUser(anyInt()))
                .thenReturn(userDto);
        Mockito.when(bookingService.getBookings(anyInt(), anyString()))
                .thenReturn(List.of(bookingExtendedDto));

        Mockito.when(commentRepository.save(any()))
                .thenReturn(new Comment(1, commentDto.getText(), item, UserMapper.toUser(userDto), LocalDateTime.now()));

        CommentDto returned = itemService.createComment(commentDto, item.getId(), 2);
        Assertions.assertEquals(returned.getId(), commentDto.getId());
        Assertions.assertEquals(returned.getText(), commentDto.getText());

    }

    @Test
    void createCommentWithoutBookings() {
        ItemDto created = createItemDto();
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(java.util.Optional.ofNullable(item));
        Mockito.when(userService.getUser(anyInt()))
                .thenReturn(userDto);
        Mockito.when(bookingService.getBookings(anyInt(), anyString()))
                .thenReturn(List.of());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, item.getId(), 2));
    }

    @Test
    void createCommentToNotFoundItem() {
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class,
                () -> itemService.createComment(commentDto, 99, 2));

    }

    @Test
    void createCommentWithEmptyText() {
        CommentDto commentDto = new CommentDto(1, "", itemDto.getId(), userDto.getName(), LocalDateTime.now());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, itemDto.getId(), 2));
        Assertions.assertEquals("Текст комментария не может быть пустым", ex.getMessage());
    }

    @Test
    void searchItemsWithNullText() {
        CommentDto commentDto = new CommentDto(1, null, itemDto.getId(), userDto.getName(), LocalDateTime.now());
        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.createComment(commentDto, itemDto.getId(), 2));
        Assertions.assertEquals("Текст комментария не может быть пустым", ex.getMessage());
    }

    @Test
    void getComments() {
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        Comment comment = new Comment(1, commentDto.getText(), item, UserMapper.toUser(userDto), LocalDateTime.now());


        Mockito.when(commentRepository.findCommentByItem_IdIsOrderByCreated(anyInt()))
                .thenReturn(List.of(comment));

        List<CommentDto> returned = itemService.getComments(item.getId());
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), comment.getId());

    }

    @Test
    void getItemsComments() {
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        Comment comment = new Comment(1, commentDto.getText(), item, UserMapper.toUser(userDto), LocalDateTime.now());


        Mockito.when(commentRepository.findCommentByItem_IdInOrderByCreated(anyList()))
                .thenReturn(List.of(comment));

        List<CommentDto> returned = itemService.getItemsComments(List.of(item.getId()));
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), comment.getId());
    }

    @Test
    void getAllComments() {
        CommentDto commentDto = new CommentDto(1, "text", itemDto.getId(), userDto.getName(), LocalDateTime.now());

        Comment comment = new Comment(1, commentDto.getText(), item, UserMapper.toUser(userDto), LocalDateTime.now());


        Mockito.when(commentRepository.findAll())
                .thenReturn(List.of(comment));

        List<CommentDto> returned = itemService.getAllComments();
        Assertions.assertEquals(returned.size(), 1);
        Assertions.assertEquals(returned.get(0).getId(), comment.getId());
    }
}