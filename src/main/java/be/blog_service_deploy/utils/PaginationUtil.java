package be.blog_service_deploy.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public class PaginationUtil {
    public static Pageable createPageable(Map<String, String> params) {
        int page;
        int size;
        String sortBy = params.getOrDefault("sortBy", "id");
        String sortDir = params.getOrDefault("sortDir", "asc");

        try {
            page = Integer.parseInt(params.getOrDefault("page", "0"));
            size = Integer.parseInt(params.getOrDefault("size", "10"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid page or size parameter");
        }

        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    public static <T> Map<String, Object> createResponse(Page<T> page) {
        return Map.of(
                "content", page.getContent(),
                "currentPage", page.getNumber(),
                "totalItems", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "isLast", page.isLast()
        );
    }
}
