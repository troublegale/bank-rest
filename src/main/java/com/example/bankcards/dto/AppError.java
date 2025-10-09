package com.example.bankcards.dto;

import java.util.Date;
import java.util.List;

public record AppError(
        Integer code,
        List<String> messages,
        Date timestamp
) {
}
