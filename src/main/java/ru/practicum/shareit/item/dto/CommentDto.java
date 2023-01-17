package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Integer id;
    private final String text;
    private final Integer itemId;
    private final String authorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-DD'T'HH:mm:ss")
    private final LocalDateTime created;
}