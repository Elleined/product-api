package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.response.SaleRetailProductResponse;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleRetailProductMapperTest {

    @Spy
    private RetailProductService retailProductService;

    @InjectMocks
    private SaleRetailProductMapper saleRetailProductMapper = Mappers.getMapper(SaleRetailProductMapper.class);

    @Test
    void toEntity() {
        // Expected/ Actual values

        // Mock data
        int quantityPerUnit = 10;
        int pricePerUnit = 10;

        // Stubbing methods

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .availableQuantity(90)
                .build();

        // Calling the method
        SaleRetailProduct saleRetailProduct = saleRetailProductMapper.toEntity(retailProduct, quantityPerUnit, pricePerUnit);
        // Assertions
        assertEquals(0, saleRetailProduct.getId());
        assertEquals(quantityPerUnit, saleRetailProduct.getQuantityPerUnit());
        assertEquals(pricePerUnit, saleRetailProduct.getPricePerUnit());
        assertNotNull(saleRetailProduct.getRetailProduct());
        assertNotNull(saleRetailProduct.getCreatedAt());
        assertNotNull(saleRetailProduct.getUpdatedAt());
        // Behavior verification
    }


    @Test
    void toDTO() {
        // Expected value
        double expectedSalePrice = 100;
        double expectedTotalPrice = 100;
        int expectedSalePercentage = 9;

        // Mock Data
        SaleRetailProduct expected = SaleRetailProduct.saleRetailProductBuilder()
                .id(1)
                .quantityPerUnit(1)
                .pricePerUnit(1)
                .retailProduct(RetailProduct.retailProductBuilder()
                        .availableQuantity(90)
                        .build())
                .build();

        // Stubbing methods
        when(retailProductService.calculateTotalPrice(anyDouble(), anyInt(), anyInt())).thenReturn(expectedSalePrice);
        when(retailProductService.calculateTotalPrice(any(RetailProduct.class))).thenReturn(expectedTotalPrice);
        when(retailProductService.getSalePercentage(anyDouble(), anyDouble())).thenReturn(expectedSalePercentage);
        // Calling the method
        // Assestions
        SaleRetailProductResponse actual = saleRetailProductMapper.toDTO(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getQuantityPerUnit(), actual.getQuantityPerUnit());
        assertEquals(expected.getPricePerUnit(), actual.getPricePerUnit());
        assertEquals(expectedSalePrice, actual.getSalePrice());
        assertEquals(expectedSalePercentage, actual.getSalePercentage());
        // Behavior verification
    }
}