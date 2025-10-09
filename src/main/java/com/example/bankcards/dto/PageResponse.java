package com.example.bankcards.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        Integer page,
        Integer size,
        Long totalElements,
        Integer totalPages
) {

}
