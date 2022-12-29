package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item, Integer userId);

    ItemDto updateItem(ItemDto item, Integer userId);

    ItemExtendedDto getItem(Integer id);

    void deleteItem(Integer id);

    List<ItemExtendedDto> getItems(Integer ownerId);

    List<ItemDto> searchItems(String text, Integer ownerId);

    CommentDto createComment(CommentDto comment, Integer itemId, Integer userId);

    List<CommentDto> getComments(Integer itemId);

    List<CommentDto> getAllComments();
}
