package com.elleined.marketplaceapi.service.cart.retail;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.product.ProductHasAcceptedOrderException;
import com.elleined.marketplaceapi.exception.product.ProductHasPendingOrderException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.elleined.marketplaceapi.model.order.Order.Status.*;
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

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(activeAndListedRetailProduct)
                .createdAt(LocalDateTime.now())
                .build();

        List<RetailCartItem> rawRetailCartItems = Arrays.asList(
                retailCartItem,
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
        List<RetailCartItem> expected = Arrays.asList(retailCartItem);

        assertEquals(expected.size(), actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void delete() {
        User user = User.builder()
                .retailCartItems(new ArrayList<>())
                .build();
        RetailCartItem retailCartItem = new RetailCartItem();
        user.getRetailCartItems().add(retailCartItem);

        doAnswer(i -> user.getRetailCartItems().remove(retailCartItem))
                .when(retailCartItemRepository)
                .delete(retailCartItem);

        retailCartItemService.delete(user, retailCartItem);
        List<RetailCartItem> actual = user.getRetailCartItems();
        List<RetailCartItem> expected = Collections.emptyList();

        verify(retailCartItemRepository).delete(retailCartItem);
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldThrowProductExpiredException() {
        User user = new User();
        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        RetailProduct expiredRetailProduct = RetailProduct.retailProductBuilder()
                .id(1)
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        when(retailProductService.getById(1)).thenReturn(expiredRetailProduct);

        assertThrows(ProductExpiredException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        verifyNoInteractions(retailCartItemMapper, retailCartItemRepository);
    }

    @Test
    void shouldThrowProductAlreadyInCartException() {
        User user = User.builder()
                .retailCartItems(new ArrayList<>())
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        RetailProduct retailProduct = mock(RetailProduct.class);
        retailProduct.setId(1);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();
        user.getRetailCartItems().add(retailCartItem);

        when(retailProductService.getById(1)).thenReturn(retailProduct);
        when(retailProduct.isExpired()).thenReturn(false);

        assertThrows(AlreadyExistException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        verifyNoInteractions(retailCartItemMapper, retailCartItemRepository);
    }

    @Test
    void shouldThrowProductHasPendingOrderException() {
        User user = spy(User.class);
        user.setRetailCartItems(new ArrayList<>());

        RetailProduct retailProduct = mock(RetailProduct.class);
        retailProduct.setId(1);

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        List<RetailOrder> retailOrders = Arrays.asList(
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(PENDING)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(SOLD)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(CANCELLED)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(REJECTED)
                        .build()
        );
        when(user.getRetailOrders()).thenReturn(retailOrders);

        when(retailProductService.getById(1)).thenReturn(retailProduct);
        when(retailProduct.isExpired()).thenReturn(false);
        when(user.isProductAlreadyInCart(retailProduct)).thenReturn(false);

        assertThrowsExactly(ProductHasPendingOrderException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        verifyNoInteractions(retailCartItemMapper, retailCartItemRepository);
    }

    @Test
    void shouldThrowProductHasAcceptedOrderException() {
        User user = spy(User.class);
        user.setRetailCartItems(new ArrayList<>());

        RetailProduct retailProduct = mock(RetailProduct.class);
        retailProduct.setId(1);

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        List<RetailOrder> retailOrders = Arrays.asList(
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(ACCEPTED)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(SOLD)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(CANCELLED)
                        .build(),
                RetailOrder.retailOrderBuilder()
                        .retailProduct(retailProduct)
                        .status(REJECTED)
                        .build()
        );
        when(user.getRetailOrders()).thenReturn(retailOrders);

        when(retailProductService.getById(1)).thenReturn(retailProduct);
        when(retailProduct.isExpired()).thenReturn(false);
        when(user.isProductAlreadyInCart(retailProduct)).thenReturn(false);
        when(user.hasOrder(retailProduct, PENDING)).thenReturn(false);

        assertThrowsExactly(ProductHasAcceptedOrderException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        verifyNoInteractions(retailCartItemMapper, retailCartItemRepository);
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