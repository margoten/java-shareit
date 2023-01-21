package ru.practicum.shareit.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationUtils {
    public static PageRequest createPageRequest(Integer from, Integer size, Sort sort) {
        return PageRequest.of(from / size, size, sort);
    }
}
