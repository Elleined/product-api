package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleRetailProductRequest;
import com.elleined.marketplaceapi.dto.product.sale.response.SaleRetailProductResponse;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SaleRetailProductMapperTest {

    @InjectMocks
    private SaleRetailProductMapper saleRetailProductMapper = Mappers.getMapper(SaleRetailProductMapper.class);

    @Test
    void toEntity() {
        // Expected/ Actual values

        // Mock data
        SaleRetailProductRequest saleRetailProductRequest = SaleRetailProductRequest.saleRetailProductRequestBuilder()
                .salePercentage(90)
                .quantityPerUnit(8)
                .pricePerUnit(80)
                .build();
        // Stubbing methods

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .availableQuantity(90)
                .build();

        // Calling the method
        SaleRetailProduct saleRetailProduct = saleRetailProductMapper.toEntity(saleRetailProductRequest, retailProduct);
        // Assertions
        assertEquals(0, saleRetailProduct.getId());
        assertEquals(saleRetailProductRequest.getSalePercentage(), saleRetailProduct.getSalePercentage());
        assertEquals(saleRetailProductRequest.getQuantityPerUnit(), saleRetailProduct.getQuantityPerUnit());
        assertEquals(saleRetailProductRequest.getPricePerUnit(), saleRetailProduct.getPricePerUnit());
        assertNotNull(saleRetailProduct.getRetailProduct());
        assertNotNull(saleRetailProduct.getCreatedAt());
        assertNotNull(saleRetailProduct.getUpdatedAt());
        // Behavior verification
    }


    @Test
    void toDTO() {
        // Expected value
        double expectedSalePrice = 100;

        // Mock Data
        SaleRetailProduct expected = SaleRetailProduct.saleRetailProductBuilder()
                .id(1)
                .quantityPerUnit(1)
                .pricePerUnit(1)
                .salePercentage(1)
                .build();

        SaleRetailProductResponse actual = saleRetailProductMapper.toDTO(expected, expectedSalePrice);
        // Stubbing methods

        // Calling the method
        // Assestions
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getQuantityPerUnit(), actual.getQuantityPerUnit());
        assertEquals(expected.getPricePerUnit(), actual.getPricePerUnit());
        assertEquals(expected.getSalePercentage(), actual.getSalePercentage());
        assertEquals(expectedSalePrice, actual.getSalePrice());

        // Behavior verification
    }
}