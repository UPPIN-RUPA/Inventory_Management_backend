package com.springboot.inventorymanagement.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class PaginationUtils {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private PaginationUtils() {
    }

    public static Pageable normalize(Pageable pageable, Sort defaultSort) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        if (request != null && (request.getParameter("page") != null || request.getParameter("size") != null)) {
            return normalizeFromRequest(request, defaultSort);
        }

        if (pageable == null) {
            return PageRequest.of(DEFAULT_PAGE, DEFAULT_SIZE, defaultSort);
        }

        int page = pageable.isPaged() ? pageable.getPageNumber() : DEFAULT_PAGE;
        int size = pageable.isPaged() ? pageable.getPageSize() : DEFAULT_SIZE;

        validate(page, size);

        Sort sort = pageable.getSort() != null && pageable.getSort().isSorted() ? pageable.getSort() : defaultSort;
        return PageRequest.of(page, size, sort);
    }

    private static Pageable normalizeFromRequest(HttpServletRequest request, Sort defaultSort) {
        int page = parseOrDefault(request.getParameter("page"), DEFAULT_PAGE);
        int size = parseOrDefault(request.getParameter("size"), DEFAULT_SIZE);
        validate(page, size);
        return PageRequest.of(page, size, defaultSort);
    }

    private static int parseOrDefault(String rawValue, int defaultValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(rawValue);
    }

    private static void validate(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be zero or greater");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        if (size > MAX_SIZE) {
            throw new IllegalArgumentException("Page size must not be greater than 100");
        }
    }
}
