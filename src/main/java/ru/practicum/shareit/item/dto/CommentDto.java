package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentDto {
    private int id;
    private String text;
    private Integer itemId;
    private Integer authorId;
    @DateTimeFormat(pattern = "hh:mm yyyy-MM-dd")
    private LocalDateTime created;
}