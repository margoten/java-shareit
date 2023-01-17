package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private UserDto itemOwner;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemOwner = userService.createUser(new UserDto(null, "Harry", "mail@mail.ru"));
        itemDto = itemService.createItem(new ItemDto(null,
                "Item",
                "Description",
                true, itemOwner.getId(), null), null, itemOwner.getId());
    }

    private CommentDto createCommentDto(String commentText, UserDto userDto) {
        UserDto booker = userService.createUser(userDto);
        bookingService.createBooking(
                new BookingCreateDto(null, LocalDateTime.now().minusSeconds(2), LocalDateTime.now().minusSeconds(1), itemDto.getId(), booker.getId(), null),
                new ItemExtendedDto(itemDto.getId(),
                        itemDto.getName(),
                        itemDto.getDescription(),
                        true, itemOwner.getId(), null, null, null, List.of()), booker.getId());
        CommentDto dto = new CommentDto(null, commentText, itemDto.getId(), booker.getName(), LocalDateTime.now());
        return itemService.createComment(dto, dto.getItemId(), booker.getId());
    }


    @Test
    void createItem() {

        Item item = entityManager.createQuery("Select i from Item i", Item.class).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(itemDto.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void updateItem() {
        ItemDto updated = new ItemDto(itemDto.getId(),
                "Item1",
                "Description",
                false, itemOwner.getId(), null);
        itemService.updateItem(updated, itemDto.getOwnerId());
        Item item = entityManager.createQuery("Select i from Item i", Item.class).getSingleResult();

        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(updated.getName()));
        assertThat(item.getDescription(), equalTo(updated.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(updated.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(updated.getAvailable()));
    }

    @Test
    void getItem() {
        ItemExtendedDto returned = itemService.getItem(itemDto.getId(), itemDto.getOwnerId());
        Item item = entityManager.createQuery("Select i from Item i where i.id = :id and i.owner.id = :ownerId", Item.class)
                .setParameter("id", itemDto.getId())
                .setParameter("ownerId", itemDto.getOwnerId())
                .getSingleResult();
        assertThat(item.getId(), notNullValue());
        assertThat(item.getName(), equalTo(returned.getName()));
        assertThat(item.getDescription(), equalTo(returned.getDescription()));
        assertThat(item.getOwner().getId(), equalTo(returned.getOwnerId()));
        assertThat(item.getAvailable(), equalTo(returned.getAvailable()));
    }

    @Test
    void getItemWithNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemService.getItem(99, itemOwner.getId()));
        Assertions.assertEquals("Товара с id = " + 99 + " не существует.", ex.getMessage());
    }

    @Test
    void deleteItem() {
        itemService.deleteItem(itemDto.getId());
        List<Item> items = entityManager.createQuery("Select i from Item i where i.id = :id and i.owner.id = :ownerId", Item.class)
                .setParameter("id", itemDto.getId())
                .setParameter("ownerId", itemDto.getOwnerId())
                .getResultList();
        assertThat(items, empty());
    }

    @Test
    void getItems() {
        itemDto = itemService.createItem(new ItemDto(null,
                "Item2",
                "Description2",
                true, itemOwner.getId(), null), null, itemOwner.getId());

        List<ItemExtendedDto> itemsReturned = itemService.getItems(itemDto.getOwnerId(), 0, 2);
        List<Item> items = entityManager.createQuery("Select i from Item i where i.owner.id = :ownerId", Item.class)
                .setParameter("ownerId", itemDto.getOwnerId())
                .getResultList();
        assertThat(items.size(), equalTo(itemsReturned.size()));
        assertThat(items.get(0).getId(), equalTo(itemsReturned.get(0).getId()));
    }

    @Test
    void searchItems() {
        itemDto = itemService.createItem(new ItemDto(null,
                "Item2",
                "Description2",
                true, itemOwner.getId(), null), null, itemOwner.getId());

        List<ItemDto> itemsReturned = itemService.searchItems("item", itemDto.getOwnerId(), 0, 2);
        List<Item> items = entityManager.createQuery("select i from Item i " +
                        "where i.available = TRUE and (upper(i.name) like upper(concat('%', :text, '%')) " +
                        "or upper(i.description) like upper(concat('%', :text, '%')))", Item.class)
                .setParameter("text", "item")
                .getResultList();
        assertThat(items.size(), equalTo(itemsReturned.size()));
        assertThat(items.get(0).getId(), equalTo(itemsReturned.get(0).getId()));
    }

    @Test
    void searchItemsWithNotAvailableItem() {
        itemDto = itemService.createItem(new ItemDto(null,
                "Item2",
                "Description2",
                false, itemOwner.getId(), null), null, itemOwner.getId());

        List<ItemDto> itemsReturned = itemService.searchItems("item2", itemDto.getOwnerId(), 0, 2);
        List<Item> items = entityManager.createQuery("select i from Item i " +
                        "where i.available = TRUE and (upper(i.name) like upper(concat('%', :text, '%')) " +
                        "or upper(i.description) like upper(concat('%', :text, '%')))", Item.class)
                .setParameter("text", "item2")
                .getResultList();
        assertThat(items.size(), equalTo(itemsReturned.size()));
        assertThat(items, empty());
    }

    @Test
    void searchItemsWithEmptyResultByText() {
        itemDto = itemService.createItem(new ItemDto(null,
                "Item2",
                "Description2",
                true, itemOwner.getId(), null), null, itemOwner.getId());

        List<ItemDto> itemsReturned = itemService.searchItems("hello", itemDto.getOwnerId(), 0, 2);
        List<Item> items = entityManager.createQuery("select i from Item i " +
                        "where i.available = TRUE and (upper(i.name) like upper(concat('%', :text, '%')) " +
                        "or upper(i.description) like upper(concat('%', :text, '%')))", Item.class)
                .setParameter("text", "hello")
                .getResultList();
        assertThat(items.size(), equalTo(itemsReturned.size()));
        assertThat(items, empty());
    }

    @Test
    void createComment() {
        CommentDto returned = createCommentDto("Comment", new UserDto(null, "Harry", "booker@mail.ru"));
        Comment comment = entityManager.createQuery("Select c from Comment c", Comment.class).getSingleResult();

        assertThat(comment.getId(), notNullValue());
        assertThat(comment.getId(), equalTo(returned.getId()));
        assertThat(comment.getText(), equalTo(returned.getText()));
        assertThat(comment.getItem().getId(), equalTo(returned.getItemId()));
        assertThat(comment.getAuthor().getName(), equalTo(returned.getAuthorName()));
    }

    @Test
    void getComments() {
        CommentDto returned = createCommentDto("Comment", new UserDto(null, "Harry", "booker@mail.ru"));

        List<CommentDto> returnedComments = itemService.getComments(itemDto.getId());

        List<Comment> comments = entityManager.createQuery("Select c from Comment c where c.item.id = :itemId", Comment.class)
                .setParameter("itemId", itemDto.getId())
                .getResultList();

        assertThat(comments, Matchers.notNullValue());
        assertThat(comments.size(), equalTo(returnedComments.size()));
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0).getId(), equalTo(returnedComments.get(0).getId()));
    }

    @Test
    void getItemsComments() {
        CommentDto returned = createCommentDto("Comment2", new UserDto(null, "Harry", "booker@mail.ru"));

        List<CommentDto> returnedComments = itemService.getItemsComments(List.of(itemDto.getId()));

        List<Comment> comments = entityManager.createQuery("Select c from Comment c where c.item.id in :itemIds", Comment.class)
                .setParameter("itemIds", List.of(itemDto.getId()))
                .getResultList();

        assertThat(comments, Matchers.notNullValue());
        assertThat(comments.size(), equalTo(returnedComments.size()));
        assertThat(comments.size(), equalTo(1));
        assertThat(comments.get(0).getId(), equalTo(returnedComments.get(0).getId()));
    }

    @Test
    void getItemsCommentsWithEmptyResult() {
        List<CommentDto> returnedComments = itemService.getItemsComments(List.of(itemDto.getId()));

        List<Comment> comments = entityManager.createQuery("Select c from Comment c where c.item.id in :itemIds", Comment.class)
                .setParameter("itemIds", List.of(itemDto.getId()))
                .getResultList();

        assertThat(comments, empty());
        assertThat(comments.size(), equalTo(returnedComments.size()));
    }

    @Test
    void getAllComments() {
        CommentDto returned1 = createCommentDto("Comment", new UserDto(null, "Harry", "booker@mail.ru"));
        CommentDto returned2 = createCommentDto("Comment2", new UserDto(null, "Harry", "booker2@mail.ru"));

        List<CommentDto> returnedComments = itemService.getAllComments();

        List<Comment> comments = entityManager.createQuery("Select c from Comment c", Comment.class)
                .getResultList();

        assertThat(comments, Matchers.notNullValue());
        assertThat(comments.size(), equalTo(returnedComments.size()));
        assertThat(comments.size(), equalTo(2));
    }

    @Test
    void getItemsByRequestId() {
        UserDto requestor = userService.createUser(new UserDto(null, "Harry", "requestor@mail.ru"));
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(new ItemRequestDto(null, "Hello", requestor.getId(), LocalDateTime.now(), List.of()), requestor.getId());
        ItemDto itemWithRequest = itemService.createItem(new ItemDto(null,
                "Item",
                "Description",
                true, itemOwner.getId(), null), itemRequestDto, itemOwner.getId());

        List<ItemDto> returnedItems = itemService.getItemsByRequestId(itemRequestDto.getId());
        List<Item> itemsByRequest = entityManager.createQuery("Select i from Item i where i.request.id = :requestId", Item.class)
                .setParameter("requestId", itemRequestDto.getId())
                .getResultList();

        assertThat(returnedItems, Matchers.notNullValue());
        assertThat(returnedItems.size(), equalTo(itemsByRequest.size()));
        assertThat(returnedItems.size(), equalTo(1));
    }

    @Test
    void getItemsByRequestIdWithEmptyResult() {
        UserDto requestor = userService.createUser(new UserDto(null, "Harry", "requestor@mail.ru"));
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(new ItemRequestDto(null, "Hello", requestor.getId(), LocalDateTime.now(), List.of()), requestor.getId());

        List<ItemDto> returnedItems = itemService.getItemsByRequestId(itemRequestDto.getId());
        List<Item> itemsByRequest = entityManager.createQuery("Select i from Item i where i.request.id = :requestId", Item.class)
                .setParameter("requestId", itemRequestDto.getId())
                .getResultList();

        assertThat(returnedItems, empty());
        assertThat(returnedItems.size(), equalTo(itemsByRequest.size()));
    }

    @Test
    void getItemsByRequests() {
        UserDto requestor = userService.createUser(new UserDto(null, "Harry", "requestor@mail.ru"));
        ItemRequestDto itemRequestDto = itemRequestService.createItemRequest(new ItemRequestDto(null, "Hello", requestor.getId(), LocalDateTime.now(), List.of()), requestor.getId());
        ItemDto itemWithRequest = itemService.createItem(new ItemDto(null,
                "Item",
                "Description",
                true, itemOwner.getId(), null), itemRequestDto, itemOwner.getId());

        List<ItemDto> returnedItems = itemService.getItemsByRequests(List.of(itemRequestDto.getId()));
        List<Item> itemsByRequest = entityManager.createQuery("Select i from Item i where i.request.id in :requestIds", Item.class)
                .setParameter("requestIds", List.of(itemRequestDto.getId()))
                .getResultList();

        assertThat(returnedItems, Matchers.notNullValue());
        assertThat(returnedItems.size(), equalTo(itemsByRequest.size()));
        assertThat(returnedItems.size(), equalTo(1));
    }
}