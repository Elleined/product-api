package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.*;
import static com.elleined.marketplaceapi.model.product.Product.SaleStatus.NOT_ON_SALE;
import static com.elleined.marketplaceapi.model.product.Product.State.*;
import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleCartItemServiceImplTest {

    @Mock
    private WholeSaleProductServiceImpl wholeSaleProductService;
    @Mock
    private WholeSaleCartItemMapper wholeSaleCartItemMapper;
    @Mock
    private WholeSaleCartItemRepository wholeSaleCartItemRepository;
    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private WholeSaleCartItemServiceImpl wholeSaleCartItemService;

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    void getAll(String productStatus) {
        User user = new User();

        WholeSaleCartItem activeAndListing = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.LISTING)
                        .status(Status.ACTIVE)
                        .build())
                .build();

        WholeSaleCartItem activeAndNotListed = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.valueOf(productStatus))
                        .status(Status.ACTIVE)
                        .build())
                .build();

        WholeSaleCartItem inActiveAndNotListed = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.valueOf(productStatus))
                        .status(Status.INACTIVE)
                        .build())
                .build();

        List<WholeSaleCartItem> rawWholeSaleCartItems = Arrays.asList(activeAndListing, activeAndNotListed, inActiveAndNotListed);
        user.setWholeSaleCartItems(rawWholeSaleCartItems);

        List<WholeSaleCartItem> actual = wholeSaleCartItemService.getAll(user);
        List<WholeSaleCartItem> expected = Collections.singletonList(activeAndListing);

        assertIterableEquals(expected, actual);
    }

    @Test
    void delete() {
        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .id(1)
                        .build())
                .build();

        wholeSaleCartItemService.delete(wholeSaleCartItem);
        verify(wholeSaleCartItemRepository).delete(wholeSaleCartItem);
    }


    @Test
    void save() {
        User user = User.builder()
                .id(1)
                .wholeSaleProducts(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .deliveryAddresses(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();
        user.getDeliveryAddresses().add(deliveryAddress);

        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .id(1)
                .description("Description")
                .availableQuantity(1_000)
                .harvestDate(LocalDate.now())
                .listingDate(LocalDateTime.now())
                .picture("Picture.jpg")
                .state(EXPIRED)
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
                .price(new BigDecimal(5_000))
                .privateChatRooms(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .deliveryAddressId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = new WholeSaleCartItem();

        when(wholeSaleProductService.getById(dto.getProductId())).thenReturn(wholeSaleProduct);
        when(addressService.getDeliveryAddressById(user, dto.getDeliveryAddressId())).thenReturn(deliveryAddress);
        when(wholeSaleCartItemMapper.toEntity(dto, user, deliveryAddress, wholeSaleProduct)).thenReturn(wholeSaleCartItem);
        when(wholeSaleCartItemRepository.save(wholeSaleCartItem)).thenReturn(wholeSaleCartItem);

        wholeSaleCartItemService.save(user, dto);

        verify(wholeSaleProductService).isRejectedBySeller(user, wholeSaleProduct);
        verify(addressService).getDeliveryAddressById(user, dto.getDeliveryAddressId());
        verify(wholeSaleCartItemMapper).toEntity(dto, user, deliveryAddress, wholeSaleProduct);
        verify(wholeSaleCartItemRepository).save(wholeSaleCartItem);
        assertDoesNotThrow(() -> wholeSaleCartItemService.save(user, dto));
    }

    @Test
    void orderCartItem() {
    }

    @Test
    void getByProduct() {
    }

}