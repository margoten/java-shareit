package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.error.ValidationException;

public class PaginationUtils {
    public static PageRequest createPageRequest(Integer from, Integer size, Sort sort) {
        if (from < 0 || size <= 0) {
            throw new ValidationException("Некорректное значение для постраничной загрузки");
        }
        return PageRequest.of(from / size, size, sort);
    }
}
