package com.elleined.marketplaceapi.service.file.order;

import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RetailOrderExporterTest {

    @Test
    void export() throws IOException {
        RetailOrderExporter retailOrderExporter = new RetailOrderExporter();
        List<RetailOrder> retailOrders = Arrays.asList(getMockRetailOrder(), getMockRetailOrder());
        retailOrderExporter.export(new MockHttpServletResponse(), retailOrders);
    }

    @Test
    void exportByDateRange() throws IOException {
        RetailOrderExporter retailOrderExporter = new RetailOrderExporter();
        List<RetailOrder> retailOrders = Arrays.asList(getMockRetailOrder(), getMockRetailOrder());
        retailOrderExporter.export(new MockHttpServletResponse(), retailOrders, LocalDate.now(), LocalDate.now());
    }

    RetailOrder getMockRetailOrder() {
        return RetailOrder.retailOrderBuilder()
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .seller(User.builder()
                                .id(1)
                                .build())
                        .crop(Crop.builder()
                                .name("Crop")
                                .build())
                        .build())
                .price(5000)
                .orderQuantity(100)
                .orderDate(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}