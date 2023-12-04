package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleWholeSaleResponse;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyDouble;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class SaleWholeSaleProductMapperTest {

    @Spy
    private WholeSaleProductService wholeSaleProductService;

    @InjectMocks
    private SaleWholeSaleProductMapper saleWholeSaleProductMapper = Mappers.getMapper(SaleWholeSaleProductMapper.class);

    @Test
    void toEntity() {
        // Expected/ Actual values
        double expectedSalePrice = 100;

        // Mock data

        // Stubbing methods
        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        // Calling the method
        SaleWholeSaleProduct saleWholeSaleProduct = saleWholeSaleProductMapper.toEntity(wholeSaleProduct, expectedSalePrice);

        // Assertions
        assertEquals(0, saleWholeSaleProduct.getId());
        assertEquals(expectedSalePrice, saleWholeSaleProduct.getSalePrice());
        assertNotNull(saleWholeSaleProduct.getWholeSaleProduct());
        assertNotNull(saleWholeSaleProduct.getCreatedAt());
        assertNotNull(saleWholeSaleProduct.getUpdatedAt());

        // Behavior verification
    }

    @Test
    void toDTO() {
        // Expected values
        double expectedTotalPrice = 100;
        double expectedSalePrice = 100;
        int expectedSalePercentage = 19;

        // Mock Data
        SaleWholeSaleProduct expected = SaleWholeSaleProduct.saleWholeSaleProductBuilder()
                .id(1)
                .salePrice(900)
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .price(new BigDecimal(900))
                        .build())
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getSalePercentage(anyDouble(), anyDouble())).thenReturn(expectedSalePercentage);

        SaleWholeSaleResponse actual = saleWholeSaleProductMapper.toDTO(expected);
        // Calling the method
        // Assestions
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSalePrice(), actual.getSalePrice());
        assertEquals(expectedSalePercentage, actual.getSalePercentage());
        // Behavior verification
    }
}