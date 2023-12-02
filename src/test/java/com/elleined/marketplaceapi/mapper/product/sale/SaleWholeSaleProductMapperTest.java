package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class SaleWholeSaleProductMapperTest {

    @InjectMocks
    private SaleWholeSaleProductMapper saleWholeSaleProductMapper = Mappers.getMapper(SaleWholeSaleProductMapper.class);

    @Test
    void toEntity() {
        // Expected/ Actual values

        // Mock data
        SaleWholeSaleRequest saleWholeSaleRequest = SaleWholeSaleRequest.saleWholeSaleProductRequestBuilder()
                .salePercentage(90)
                .salePrice(800)
                .build();

        // Stubbing methods
        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        // Calling the method
        SaleWholeSaleProduct saleWholeSaleProduct = saleWholeSaleProductMapper.toEntity(saleWholeSaleRequest, wholeSaleProduct);

        // Assertions
        assertEquals(0, saleWholeSaleProduct.getId());
        assertEquals(saleWholeSaleRequest.getSalePrice(), saleWholeSaleProduct.getSalePrice());
        assertEquals(saleWholeSaleRequest.getSalePercentage(), saleWholeSaleProduct.getSalePercentage());
        assertNotNull(saleWholeSaleProduct.getWholeSaleProduct());
        assertNotNull(saleWholeSaleProduct.getCreatedAt());
        assertNotNull(saleWholeSaleProduct.getUpdatedAt());

        // Behavior verification
    }
}