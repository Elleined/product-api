package com.elleined.marketplaceapi.service.file.order;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;

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