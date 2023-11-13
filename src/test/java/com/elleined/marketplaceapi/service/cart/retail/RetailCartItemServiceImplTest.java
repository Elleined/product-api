package com.elleined.marketplaceapi.service.cart.retail;

import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.RetailCartItemRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetailCartItemServiceImplTest {

    @Mock
    private RetailProductService retailProductService;
    @Mock
    private RetailCartItemMapper retailCartItemMapper;
    @Mock
    private RetailCartItemRepository retailCartItemRepository;
    @Mock
    private RetailOrderRepository retailOrderRepository;

    @InjectMocks
    private RetailCartItemServiceImpl retailCartItemService;

    @Test
    void getAll() {
        User user = new User();

        RetailProduct activeAndListedRetailProduct = RetailProduct.retailProductBuilder()
                .id(1)
                .status(Product.Status.ACTIVE)
                .state(Product.State.LISTING)
                .build();

        RetailProduct inactiveRetailProduct = RetailProduct.retailProductBuilder()
                .id(2)
                .status(Product.Status.INACTIVE)
                .state(Product.State.LISTING)
                .build();

        RetailProduct unListedRetailProduct = RetailProduct.retailProductBuilder()
                .id(3)
                .status(Product.Status.ACTIVE)
                .state(Product.State.PENDING)
                .build();

        List<RetailCartItem> rawRetailCartItems = Arrays.asList(
                RetailCartItem.retailCartItemBuilder()
                        .retailProduct(activeAndListedRetailProduct)
                        .createdAt(LocalDateTime.now())
                        .build(),
                RetailCartItem.retailCartItemBuilder()
                        .retailProduct(inactiveRetailProduct)
                        .createdAt(LocalDateTime.now())
                        .build(),
                RetailCartItem.retailCartItemBuilder()
                        .retailProduct(unListedRetailProduct)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        user.setRetailCartItems(rawRetailCartItems);

        List<RetailCartItem> actual = retailCartItemService.getAll(user);
        actual.forEach(retailCartItem -> System.out.println(retailCartItem.getRetailProduct().getId()));

        List<RetailCartItem> expected = Arrays.asList(
                RetailCartItem.retailCartItemBuilder()
                        .retailProduct(activeAndListedRetailProduct)
                        .createdAt(LocalDateTime.now())
                        .build()
        );
        expected .forEach(retailCartItem -> System.out.println(retailCartItem.getRetailProduct().getId()));

        for (int i = 0; i < actual.size() && i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
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