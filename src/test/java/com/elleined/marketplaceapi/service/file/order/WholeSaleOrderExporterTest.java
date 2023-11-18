package com.elleined.marketplaceapi.service.file.order;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleOrderExporterTest {

    @Mock
    private WholeSaleOrderExporter wholeSaleOrderExporter;
    @Test
    void export() throws IOException {
        wholeSaleOrderExporter.export(any(HttpServletResponse.class), anyList());
    }

    @Test
    void exportByDateRange() throws IOException {
        wholeSaleOrderExporter.export(any(HttpServletResponse.class), anyList(), any(LocalDate.class), any(LocalDate.class));
    }
}