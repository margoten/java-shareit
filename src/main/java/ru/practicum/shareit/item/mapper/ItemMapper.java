package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingExtendedDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDto toItemDto(ItemExtendedDto itemDto) {
        return new ItemDto(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwnerId(),
                itemDto.getRequestId()
        );
    }

    public static ItemExtendedDto toItemExtendedDto(Item item, BookingExtendedDto lastBooking, BookingExtendedDto nextBooking, List<CommentDto> comments) {
        return new ItemExtendedDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking == null ? null : new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                nextBooking == null ? null : new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()),
                comments == null ? List.of() : comments
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static Item toItem(ItemExtendedDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

}
