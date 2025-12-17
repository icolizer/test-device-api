package de.device.demo.utils;

import java.util.List;

public record PageableModelTest<T>(
        List<T> content,
        int number,
        int size,
        long totalElements,
        int totalPages
) {}
