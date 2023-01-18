package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private UserDto user;

    @BeforeEach
    void setUp() {
        user = userService.createUser(new UserDto(null, "Harry", "mail@mail.ru"));
        itemRequestDto = new ItemRequestDto(
                1,
                "Some text",
                user.getId(),
                LocalDateTime.now(),
                List.of());
    }

    private void insertItemRequest() {
        itemRequestService.createItemRequest(new ItemRequestDto(
                1,
                "Some text",
                user.getId(),
                LocalDateTime.now(),
                List.of()), user.getId());
        itemRequestService.createItemRequest(new ItemRequestDto(
                2,
                "Some text",
                user.getId(),
                LocalDateTime.now(),
                List.of()), user.getId());
    }

    @Test
    void createItemRequest() {
        itemRequestService.createItemRequest(itemRequestDto, user.getId());

        ItemRequest itemRequest = entityManager.createQuery("Select i from ItemRequest i", ItemRequest.class).getSingleResult();

        assertThat(itemRequest.getId(), notNullValue());
        assertThat(itemRequest.getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(itemRequest.getRequestor().getId(), equalTo(itemRequestDto.getRequestorId()));
    }

    @Test
    void createItemRequestWhenUserNotFound() {
        NotFoundException ex = assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestDto, 99));
        Assertions.assertEquals("Not found user with id = 99", ex.getMessage());
    }

    @Test
    void getItemRequests() {
        insertItemRequest();
        List<ItemRequestDto> returned = itemRequestService.getItemRequests(user.getId());
        List<ItemRequest> itemRequests = entityManager.createQuery("Select i from ItemRequest i where i.requestor.id = :id order by i.created desc ", ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(returned.size(), equalTo(itemRequests.size()));
        assertThat(returned.get(0).getId(), equalTo(itemRequests.get(0).getId()));
    }

    @Test
    void getItemRequest() {
        itemRequestService.createItemRequest(itemRequestDto, user.getId());
        ItemRequest itemRequest = entityManager.createQuery("Select i from ItemRequest i", ItemRequest.class).getSingleResult();

        ItemRequestDto returned = itemRequestService.getItemRequest(itemRequest.getId(), user.getId());
        assertThat(returned.getId(), equalTo(itemRequest.getId()));
        assertThat(returned.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(returned.getRequestorId(), equalTo(itemRequest.getRequestor().getId()));
    }

    @Test
    void getAllItemRequests() {
        insertItemRequest();
        UserDto userOther = userService.createUser(new UserDto(null, "Other", "mailOther@mail.ru"));
        List<ItemRequestDto> returned = itemRequestService.getAllItemRequests(0, 10, userOther.getId());

        List<ItemRequest> itemRequests = entityManager.createQuery("Select i from ItemRequest i where i.requestor.id <> :id order by i.created desc ", ItemRequest.class)
                .setParameter("id", userOther.getId())
                .getResultList();
        assertThat(returned.size(), equalTo(itemRequests.size()));
        assertThat(returned.get(0).getId(), equalTo(itemRequests.get(0).getId()));
    }

    @Test
    void getAllItemRequestsByOwner() {
        insertItemRequest();
        List<ItemRequestDto> returned = itemRequestService.getAllItemRequests(0, 10, user.getId());

        List<ItemRequest> itemRequests = entityManager.createQuery("Select i from ItemRequest i where i.requestor.id <> :id order by i.created desc ", ItemRequest.class)
                .setParameter("id", user.getId())
                .getResultList();
        assertThat(returned.size(), equalTo(itemRequests.size()));
        assertThat(returned.size(), equalTo(0));
    }
}