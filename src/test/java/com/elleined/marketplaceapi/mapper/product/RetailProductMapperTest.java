package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.elleined.marketplaceapi.model.product.Product.SaleStatus.NOT_ON_SALE;
import static com.elleined.marketplaceapi.model.product.Product.State.PENDING;
import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@ExtendWith(MockitoExtension.class)
class RetailProductMapperTest {

    @InjectMocks
    private RetailProductMapper retailProductMapper = Mappers.getMapper(RetailProductMapper.class);

    @Test
    void toDTO() {
        RetailProduct expected = RetailProduct.retailProductBuilder()
                .id(1)
                .description("Description")
                .availableQuantity(1_000)
                .harvestDate(LocalDate.now())
                .listingDate(LocalDateTime.now())
                .picture("Picture.jpg")
                .state(PENDING)
                .status(ACTIVE)
                .seller(User.builder()
                        .id(1)
                        .userDetails(UserDetails.builder()
                                .firstName("First name")
                                .middleName("Middle name")
                                .lastName("Last name")
                                .build())
                        .shop(Shop.builder()
                                .id(1)
                                .name("Shop name")
                                .build())
                        .build())
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .saleStatus(NOT_ON_SALE)
                .pricePerUnit(50)
                .quantityPerUnit(100)
                .expirationDate(LocalDate.now())
                .retailUnit(RetailUnit.retailUnitBuilder()
                        .id(1)
                        .name("Retail unit")
                        .build())
                .retailOrders(new ArrayList<>())
                .retailCartItems(new ArrayList<>())
                .privateChatRooms(new ArrayList<>())
                .build();

        double expectedTotalPrice = 5_000;
        RetailProductDTO actual = retailProductMapper.toDTO(expected, expectedTotalPrice);

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
        assertEquals(expectedTotalPrice, actual.getTotalPrice());// total price
        assertEquals(expected.getPricePerUnit(), actual.getPricePerUnit());
        assertEquals(expected.getQuantityPerUnit(), actual.getQuantityPerUnit());
    }

    @Test
    void toEntity() {
        User seller = User.builder()
                .id(1)
                .build();

        Crop crop = Crop.builder()
                .id(1)
                .name("Crop")
                .build();

        RetailUnit retailUnit = RetailUnit.retailUnitBuilder()
                .id(1)
                .name("Retail unit")
                .build();

        RetailProductDTO expected = RetailProductDTO.retailProductDTOBuilder()
                .cropName("Crop name")
                .unitId(1)
                .description("Description")
                .availableQuantity(100)
                .pricePerUnit(500)
                .quantityPerUnit(50)
                .harvestDate(LocalDate.now())
                .expirationDate(LocalDate.now())
                .build();

        RetailProduct actual = retailProductMapper.toEntity(expected, seller, crop, retailUnit, "Prodict PIcture");

        assertEquals(0, actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertNotNull(actual.getHarvestDate());
        assertNotNull(actual.getListingDate());
        assertNotNull(actual.getPicture());
        assertEquals(PENDING, actual.getState());
        assertEquals(ACTIVE, actual.getStatus());
        assertNotNull(actual.getCrop());
        assertEquals(NOT_ON_SALE, actual.getSaleStatus());
        assertEquals(expected.getPricePerUnit(), actual.getPricePerUnit());
        assertEquals(expected.getQuantityPerUnit(), actual.getQuantityPerUnit());
        assertNotNull(actual.getExpirationDate());
        assertNotNull(actual.getRetailUnit());
        assertNotNull(actual.getRetailOrders());
        assertNotNull(actual.getRetailCartItems());
        assertNotNull(actual.getPrivateChatRooms());
    }

    @Test
    void toUpdate() {
    }
}