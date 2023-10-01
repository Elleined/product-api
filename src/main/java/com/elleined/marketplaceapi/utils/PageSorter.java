package com.elleined.marketplaceapi.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface PageSorter {
    static Pageable getPage(int pageNumber, int pageSize) {
        return PageRequest.of(pageNumber, pageSize);
    }

    static Pageable getPage(int pageNumber, int pageSize, Sort.Direction sortDirection, String sortProperty) {
        return PageRequest.of(pageNumber, pageSize, sortDirection, sortProperty);
    }
}
