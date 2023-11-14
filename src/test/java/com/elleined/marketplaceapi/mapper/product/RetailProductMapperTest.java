package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class RetailProductMapperTest {

    @InjectMocks
    private RetailProductMapper retailProductMapper = Mappers.getMapper(RetailProductMapper.class);

    @Test
    void toDTO() {
        User seller = User.builder()
                .id(1)
                .build();

        RetailProduct expected = RetailProduct.retailProductBuilder()
                .id(1)
                .description("Description")
                .availableQuantity(1_000)
                .harvestDate(LocalDate.now())
                .listingDate(LocalDateTime.now())
                .picture("Picture.jpg")
                .state(Product.State.PENDING)
                .status(Product.Status.ACTIVE)
                .seller(seller)
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .saleStatus(Product.SaleStatus.NOT_ON_SALE)
                .pricePerUnit(50)
                .quantityPerUnit(100)
                .expirationDate(LocalDate.now())
                .retailUnit(RetailUnit.retailUnitBuilder()
                        .name("Retail unit")
                        .build())
                .retailOrders(new ArrayList<>())
                .retailCartItems(new ArrayList<>())
                .privateChatRooms(new ArrayList<>())
                .build();

        RetailProductDTO actual = retailProductMapper.toDTO(expected);

        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCrop().getName(), actual.getCropName());
        assertEquals(expected.getRetailUnit().getId(), actual.getUnitId());
        assertEquals(expected.getRetailUnit().getName(), actual.getUnitName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPicture(), actual.getPicture());
        assertEquals(expected.getState().name(), actual.getState());
        assertEquals(expected.getSeller().getId(), actual.getSellerId());
        assertEquals(expected.getSeller().getFullName(), actual.getSellerName());
        assertEquals(expected.getSaleStatus().name(), actual.getSaleStatus());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertNotNull(actual.getHarvestDate());
        assertNotNull(actual.getListingDate());
        assertNotNull(actual.getExpirationDate());
        assertEquals(expected.getSeller().getShop().getName(), actual.getShopName());
        // assertEquals();// total price
        assertEquals(expected.getPricePerUnit(), actual.getPricePerUnit());
        assertEquals(expected.getQuantityPerUnit(), actual.getQuantityPerUnit());
    }

    @Test
    void toEntity() {
    }

    @Test
    void toUpdate() {
    }
}