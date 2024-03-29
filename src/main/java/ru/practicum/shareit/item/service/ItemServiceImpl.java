package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.utils.PaginationUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private static final Pageable PAGEABLE_DEFAULT = PaginationUtils.createPageRequest(0, 100, Sort.by("id").ascending());

    @Override
    public ItemDto createItem(ItemDto itemDto, ItemRequestDto itemRequestDto, Integer ownerId) {
        validationItem(itemDto);
        User owner = UserMapper.toUser(userService.getUser(ownerId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemRequestDto != null) {
            item.setRequest(ItemRequestMapper.toItemRequest(itemRequestDto, userService.getUser(itemRequestDto.getRequestorId())));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("Не заполненное поле владельца");
        }
        Item exist = getItemFromDB(itemDto.getId());
        if (!exist.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("У товара другой владелец");
        }
        if (itemDto.getName() != null) {
            exist.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            exist.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            exist.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(exist));

    }

    @Override
    public ItemExtendedDto getItem(Integer id, Integer userId) {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Товара с id = " + id + " не существует."));
        List<CommentDto> comments = getComments(id);

        List<BookingExtendedDto> bookings = bookingService.getBookingsByItem(item.getId(), userId);

        return ItemMapper.toItemExtendedDto(item,
                getLastItemBooking(bookings),
                getNextItemBooking(bookings),
                comments);
    }

    @Override
    public void deleteItem(Integer id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemExtendedDto> getItems(Integer ownerId, Integer from, Integer size) {
        if (ownerId == null) {
            throw new ValidationException("Не заполненное поле владельца");
        }
        Map<Integer, List<CommentDto>> comments = getAllComments()
                .stream().collect(Collectors.groupingBy(CommentDto::getItemId));

        Map<Integer, List<BookingExtendedDto>> bookings = bookingService.getOwnersBookings(ownerId, null, null, null)
                .stream()
                .collect(Collectors.groupingBy((BookingExtendedDto bookingExtendedDto) -> bookingExtendedDto.getItem().getId()));
        Pageable pageable = from == null || size == null
                ? PAGEABLE_DEFAULT
                : PaginationUtils.createPageRequest(from, size, Sort.by("id").ascending());
        return itemRepository.findAllByOwner_IdIs(ownerId, pageable)
                .stream()
                .map(item -> ItemMapper.toItemExtendedDto(item,
                        getLastItemBooking(bookings.get(item.getId())),
                        getNextItemBooking(bookings.get(item.getId())),
                        comments.get(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text, Integer ownerId, Integer from, Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        Pageable pageable = from == null || size == null
                ? PAGEABLE_DEFAULT
                : PaginationUtils.createPageRequest(from, size, Sort.by("id").ascending());
        return itemRepository.search(text, pageable)
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer itemId, Integer userId) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }

        Item item = getItemFromDB(itemId);
        User user = UserMapper.toUser(userService.getUser(userId));

        List<BookingExtendedDto> bookings = bookingService.getBookings(userId, Booking.TimeBookingState.PAST.name(), null, null);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может оставлять комментарий ");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));

    }

    @Override
    public List<CommentDto> getComments(Integer itemId) {
        return commentRepository.findCommentByItem_IdIsOrderByCreated(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getItemsComments(List<Integer> itemIds) {
        return commentRepository.findCommentByItem_IdInOrderByCreated(itemIds)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByRequestId(Integer requestId) {
        return itemRepository.findAllByRequest_IdIs(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByRequests(List<Integer> requests) {
        return itemRepository.findAllByRequest_IdIn(requests)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item getItemFromDB(Integer itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Товара с id = " + itemId + " не существует."));
    }

    private BookingExtendedDto getLastItemBooking(List<BookingExtendedDto> bookings) {
        return bookings == null
                ? null
                : bookings.stream()
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(BookingExtendedDto::getEnd)).orElse(null);
    }

    private BookingExtendedDto getNextItemBooking(List<BookingExtendedDto> bookings) {
        return bookings == null
                ? null
                : bookings.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(BookingExtendedDto::getEnd)).orElse(null);
    }

    private void validationItem(ItemDto item) {
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
