package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Integer id;
    @NotEmpty(message = "Текст комментария не может быть пустым")
    private final String text;
    private final Integer itemId;
    private final String authorName;
    private final LocalDateTime created;
}