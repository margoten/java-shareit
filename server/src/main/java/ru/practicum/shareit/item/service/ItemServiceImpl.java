package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, Integer ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Not found user with id = " + ownerId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() ->
                            new NotFoundException("Not found item request with id = " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer ownerId) {
        if (ownerId == null) {
            throw new ValidationException("Не заполненное поле владельца");
        }
        Item exist = itemRepository.findById(itemDto.getId()).orElseThrow(() ->
                new NotFoundException("Товара с id = " + itemDto.getId() + " не существует."));

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
        List<CommentDto> comments = commentRepository.findCommentByItem_IdIsOrderByCreated(id)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        List<BookingExtendedDto> bookings = bookingRepository.findBookingsByItem_IdAndItem_Owner_IdIsOrderByStart(id, userId)
                .stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.toList());
        ;

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
        Pageable pageable = PaginationUtils.createPageRequest(from, size, Sort.by("id").ascending());

        List<Item> items = itemRepository.findAllByOwner_IdIs(ownerId, pageable)
                .toList();
        Map<Integer, List<CommentDto>> comments = commentRepository.findCommentByItemInOrderByCreated(items)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        Map<Integer, List<BookingExtendedDto>> bookings = bookingRepository.findBookingsByItemOwner_IdIsAndItemInOrderByStartDesc(ownerId, items)
                .stream()
                .map(BookingMapper::toBookingExtendedDto)
                .collect(Collectors.groupingBy((BookingExtendedDto bookingExtendedDto) -> bookingExtendedDto.getItem().getId()));
        return items
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
        Pageable pageable = PaginationUtils.createPageRequest(from, size, Sort.by("id").ascending());
        return itemRepository.search(text, pageable)
                .stream().map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Товара с id = " + itemId + " не существует."));;
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new NotFoundException("Пользователя с id = " + userId + " не существует.");
        });

        boolean canCreateComment = bookingRepository.existsBookingByBookerIsAndEndBefore(user, LocalDateTime.now());
        if (!canCreateComment) {
            throw new ValidationException("Пользователь не может оставлять комментарий ");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));

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
}
