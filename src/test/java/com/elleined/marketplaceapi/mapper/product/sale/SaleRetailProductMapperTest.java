package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.SaleRetailProductRequest;
import com.elleined.marketplaceapi.mapper.CredentialMapper;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
}