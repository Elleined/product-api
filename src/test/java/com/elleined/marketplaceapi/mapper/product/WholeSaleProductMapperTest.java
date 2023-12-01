package com.elleined.marketplaceapi.mapper.product;

import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.elleined.marketplaceapi.model.product.Product.State.PENDING;
import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleProductMapperTest {

    @InjectMocks
    private WholeSaleProductMapper wholeSaleProductMapper = Mappers.getMapper(WholeSaleProductMapper.class);
    @Test
    void toDTO() {
        double expectedTotalPrice = 1_000;
        WholeSaleProduct expected = WholeSaleProduct.wholeSaleProductBuilder()
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
                .wholeSaleUnit(WholeSaleUnit.wholeSaleUnitBuilder()
                        .id(1)
                        .name("Whole sale unit")
                        .build())
                .price(new BigDecimal(expectedTotalPrice))
                .privateChatRooms(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        WholeSaleProductDTO actual = wholeSaleProductMapper.toDTO(expected);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCrop().getName(), actual.getCropName());
        assertEquals(expected.getWholeSaleUnit().getId(), actual.getUnitId());
        assertEquals(expected.getWholeSaleUnit().getName(), actual.getUnitName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getPicture(), actual.getPicture());
        assertEquals(expected.getState().name(), actual.getState());
        assertEquals(expected.getSeller().getId(), actual.getSellerId());
        assertEquals(expected.getSeller().getFullName(), actual.getSellerName());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertNotNull(actual.getHarvestDate());
        assertNotNull(actual.getListingDate());
        assertEquals(expected.getSeller().getShop().getName(), actual.getShopName());
        assertEquals(expectedTotalPrice, actual.getTotalPrice());
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

        WholeSaleUnit wholeSaleUnit = WholeSaleUnit.wholeSaleUnitBuilder()
                .id(1)
                .name("whole sale unit")
                .build();

        WholeSaleProductDTO expected = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .cropName("Crop name")
                .unitId(1)
                .description("Description")
                .availableQuantity(100)
                .totalPrice(10_000)
                .harvestDate(LocalDate.now())
                .build();

        WholeSaleProduct actual = wholeSaleProductMapper.toEntity(expected, seller, crop, wholeSaleUnit, "Picture");
        assertNull(actual.getSaleWholeSaleProduct());
        assertEquals(0, actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertNotNull(actual.getHarvestDate());
        assertNotNull(actual.getListingDate());
        assertNotNull(actual.getPicture());
        assertEquals(PENDING, actual.getState());
        assertEquals(ACTIVE, actual.getStatus());
        assertNotNull(actual.getCrop());
        assertNotNull(actual.getWholeSaleUnit());
        assertEquals(expected.getTotalPrice(), actual.getPrice().doubleValue());
        assertNotNull(actual.getWholeSaleOrders());
        assertNotNull(actual.getWholeSaleCartItems());
        assertNotNull(actual.getPrivateChatRooms());
    }

    @Test
    void toUpdate() {
        User seller = User.builder()
                .id(1)
                .build();

        Crop crop = Crop.builder()
                .id(1)
                .name("Crop")
                .build();

        WholeSaleUnit wholeSaleUnit = WholeSaleUnit.wholeSaleUnitBuilder()
                .id(1)
                .name("whole sale unit")
                .build();

        double expectedTotalPrice = 1_000;
        WholeSaleProduct expected = WholeSaleProduct.wholeSaleProductBuilder()
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
                .wholeSaleUnit(WholeSaleUnit.wholeSaleUnitBuilder()
                        .id(1)
                        .name("Whole sale unit")
                        .build())
                .price(new BigDecimal(expectedTotalPrice))
                .privateChatRooms(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        WholeSaleProductDTO dto = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .cropName("Crop name")
                .unitId(1)
                .description("Description")
                .availableQuantity(100)
                .totalPrice(10_000)
                .harvestDate(LocalDate.now())
                .build();

        String expectedPicture = "Changed";
        WholeSaleProduct actual = wholeSaleProductMapper.toUpdate(expected, dto, crop, wholeSaleUnit, expectedPicture);

        // Changed field should be
        assertEquals(expectedPicture, actual.getPicture());
        assertEquals(expectedPicture, expected.getPicture());


        // All field must be the same after updating
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertEquals(expected.getHarvestDate(), actual.getHarvestDate());
        assertEquals(expected.getListingDate(), actual.getListingDate());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getCrop(), actual.getCrop());
        assertEquals(expected.getWholeSaleUnit(), actual.getWholeSaleUnit());
        assertIterableEquals(expected.getWholeSaleOrders(), actual.getWholeSaleOrders());
        assertIterableEquals(expected.getWholeSaleCartItems(), actual.getWholeSaleCartItems());
        assertIterableEquals(expected.getPrivateChatRooms(), actual.getPrivateChatRooms());
    }
}