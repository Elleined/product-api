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

import static com.elleined.marketplaceapi.model.product.Product.SaleStatus.NOT_ON_SALE;
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
                .saleStatus(NOT_ON_SALE)
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
        assertEquals(expected.getSaleStatus().name(), actual.getSaleStatus());
        assertEquals(expected.getAvailableQuantity(), actual.getAvailableQuantity());
        assertNotNull(actual.getHarvestDate());
        assertNotNull(actual.getListingDate());
        assertEquals(expected.getSeller().getShop().getName(), actual.getShopName());
        assertEquals(expectedTotalPrice, actual.getTotalPrice());
    }

    @Test
    void toEntity() {
    }

    @Test
    void toUpdate() {
    }
}