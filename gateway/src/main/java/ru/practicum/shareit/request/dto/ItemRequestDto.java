package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private Integer id;
    @NotBlank(message = "Request description cannot be empty")
    private String description;
    private Integer requestorId;
    private LocalDateTime created;

}