package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleCartItemServiceImplTest {

    @Spy
    private WholeSaleProductService wholeSaleProductService;
    @Spy
    private WholeSaleCartItemMapper wholeSaleCartItemMapper;
    @Spy
    private WholeSaleCartItemRepository wholeSaleCartItemRepository;
    @Spy
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Spy
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
    }

    @Test
    void testDelete() {
    }

    @Test
    void save() {
    }

    @Test
    void orderCartItem() {
    }

    @Test
    void orderAllCartItems() {
    }

    @Test
    void getById() {
    }

    @Test
    void getByProduct() {
    }

    @Test
    void getAllById() {
    }
}