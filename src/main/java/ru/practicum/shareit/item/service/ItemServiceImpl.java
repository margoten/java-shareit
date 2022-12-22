package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;

    @Override
    public Item createItem(Item item, Integer ownerId) {
        validationItem(item);
        User owner = userService.getUser(ownerId);
        item.setOwner(owner);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Item item, Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("Не заполненное поле владельца");
        }
        Item exist = getItem(item.getId());
        if (!exist.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("У товара другой владелец");
        }
        if (item.getName() != null) {
            exist.setName(item.getName());
        }
        if (item.getDescription() != null) {
            exist.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            exist.setAvailable(item.getAvailable());
        }
        return itemRepository.save(exist);

    }

    @Override
    public Item getItem(Integer id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Товара с id = " + id + " не существует."));
    }

    @Override
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<Item> getItems(Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("Не заполненное поле владельца");
        }
        return itemRepository.findAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text, Integer ownerId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text);
    }

    @Override
    public Comment createComment(Comment comment, Integer itemId, Integer userId) {
        Item item = getItem(itemId);
        User user = userService.getUser(userId);

        List<Booking> bookings = bookingService.getBookings(userId, "PAST");
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может оставлять коментарий ");
        }
        if (comment.getText() == null || comment.getText().isBlank()) {
            throw new ValidationException("Текс комментария не может быть пустым");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);

    }

    @Override
    public List<Comment> getComments(Integer itemId) {
        Item item = getItem(itemId);
        return commentRepository.findCommentByItemIsOrderByCreated(item);
    }

    private void validationItem(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            log.warn("Название не может быть пустым.");
            throw new ValidationException("Название не может быть пустым.");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.warn("Описание не может быть пустым.");
            throw new ValidationException("Описание не может быть пустым.");
        }

        if (item.getAvailable() == null) {
            log.warn("Поле доступности пустое.");
            throw new ValidationException("Поле доступности пустое.");
        }
    }
}
