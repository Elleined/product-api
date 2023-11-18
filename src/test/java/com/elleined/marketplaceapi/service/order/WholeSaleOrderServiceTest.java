package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elleined.marketplaceapi.model.order.Order.Status.valueOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@ExtendWith(MockitoExtension.class)
class WholeSaleOrderServiceTest {

    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;

    @InjectMocks
    private WholeSaleOrderService wholeSaleOrderService;
    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    @DisplayName("Get all product by status")
    void whenGettingAllProductOrderByStatusPremiumUserOrdersMustBePrioritizeAndInDescOrder_And_RegularUserOrdersMustBeInDescOrderJustAfterThePremiumUserOldOrder(String orderStatus) {
        // Mock data
        User seller = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        WholeSaleOrder regularUserOldOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(null)
                        .build())
                .build();

        WholeSaleOrder regularUserNewOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(null)
                        .build())
                .build();

        WholeSaleOrder premiumUserOldOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(Premium.builder()
                                .registrationDate(LocalDateTime.now())
                                .build())
                        .build())
                .build();

        WholeSaleOrder premiumUserNewOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(Premium.builder()
                                .registrationDate(LocalDateTime.now())
                                .build())
                        .build())
                .build();

        WholeSaleProduct activeWholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(Arrays.asList(regularUserOldOrder, regularUserNewOrder, premiumUserOldOrder, premiumUserNewOrder))
                .status(Product.Status.ACTIVE)
                .build();

        WholeSaleProduct inActiveWholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(Arrays.asList(regularUserOldOrder, regularUserNewOrder, premiumUserOldOrder, premiumUserNewOrder))
                .status(Product.Status.INACTIVE)
                .build();

        seller.getWholeSaleProducts().add(activeWholeSaleProduct);
        seller.getWholeSaleProducts().add(inActiveWholeSaleProduct);

        // Calling the method
        List<WholeSaleOrder> actualWholeSaleOrders = wholeSaleOrderService.getAllProductOrderByStatus(seller, Order.Status.valueOf(orderStatus));
        List<WholeSaleOrder> expectedWholeSaleOrders = Arrays.asList(premiumUserNewOrder, premiumUserOldOrder, regularUserNewOrder, regularUserOldOrder);

        // Assertions
        assertIterableEquals(expectedWholeSaleOrders, actualWholeSaleOrders);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    @DisplayName("Get all ordered product by status")
    void shouldOnlyGetTheOrdersWithActiveProduct_And_MustBeInDescOrder(String orderStatus) {
        // Mock data
        User user = User.builder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleOrder activeOldWholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .status(Product.Status.ACTIVE)
                        .build())
                .build();

        WholeSaleOrder activeNewWholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .status(Product.Status.ACTIVE)
                        .build())
                .build();

        WholeSaleOrder InActiveOldWholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .status(Product.Status.INACTIVE)
                        .build())
                .build();

        WholeSaleOrder InActiveNewWholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .status(Product.Status.INACTIVE)
                        .build())
                .build();

        user.getWholeSaleOrders().add(activeOldWholeSaleOrder);
        user.getWholeSaleOrders().add(activeNewWholeSaleOrder);
        user.getWholeSaleOrders().add(InActiveOldWholeSaleOrder);
        user.getWholeSaleOrders().add(InActiveNewWholeSaleOrder);

        // Calling the method
        List<WholeSaleOrder> actualWholeSaleOrders = wholeSaleOrderService.getAllOrderedProductsByStatus(user, valueOf(orderStatus));
        List<WholeSaleOrder> expectedWholeSaleOrders = Arrays.asList(activeNewWholeSaleOrder, activeOldWholeSaleOrder);

        // Assertions
        assertIterableEquals(expectedWholeSaleOrders, actualWholeSaleOrders);
    }
}