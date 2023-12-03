package com.elleined.marketplaceapi.mapper.product.sale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.dto.product.sale.response.SaleWholeSaleResponse;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(MockitoExtension.class)
class SaleWholeSaleProductMapperTest {

    @InjectMocks
    private SaleWholeSaleProductMapper saleWholeSaleProductMapper = Mappers.getMapper(SaleWholeSaleProductMapper.class);

    @Test
    void toEntity() {
        // Expected/ Actual values
        double expectedSalePrice = 100;

        // Mock data
        SaleWholeSaleRequest saleWholeSaleRequest = SaleWholeSaleRequest.saleWholeSaleProductRequestBuilder()
                .salePercentage(90)
                .build();

        // Stubbing methods
        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        // Calling the method
        SaleWholeSaleProduct saleWholeSaleProduct = saleWholeSaleProductMapper.toEntity(wholeSaleProduct, saleWholeSaleRequest.getSalePercentage(), expectedSalePrice);

        // Assertions
        assertEquals(0, saleWholeSaleProduct.getId());
        assertEquals(expectedSalePrice, saleWholeSaleProduct.getSalePrice());
        assertEquals(saleWholeSaleRequest.getSalePercentage(), saleWholeSaleProduct.getSalePercentage());
        assertNotNull(saleWholeSaleProduct.getWholeSaleProduct());
        assertNotNull(saleWholeSaleProduct.getCreatedAt());
        assertNotNull(saleWholeSaleProduct.getUpdatedAt());

        // Behavior verification
    }

    @Test
    void toDTO() {
        // Expected values

        // Mock Data
        SaleWholeSaleProduct expected = SaleWholeSaleProduct.saleWholeSaleProductBuilder()
                .id(1)
                .salePrice(900)
                .salePercentage(1)
                .build();

        // Stubbing methods
        SaleWholeSaleResponse actual = saleWholeSaleProductMapper.toDTO(expected);
        // Calling the method
        // Assestions
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getSalePrice(), actual.getSalePrice());
        assertEquals(expected.getSalePercentage(), actual.getSalePercentage());

        // Behavior verification
    }
}