package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Integer id;
    private final String text;
    private final Integer itemId;
    private final String authorName;
    private final LocalDateTime created;
}