package com.elleined.marketplaceapi.service.file;

import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

public interface Exporter<T> {
    void export(HttpServletResponse httpServletResponse, T t) throws DocumentException, IOException;
    void export(HttpServletResponse httpServletResponse, T t, LocalDate start, LocalDate end) throws DocumentException, IOException;
}
