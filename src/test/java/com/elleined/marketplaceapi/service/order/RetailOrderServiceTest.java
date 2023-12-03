package com.elleined.marketplaceapi.service.order;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
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
class RetailOrderServiceTest {

    @Mock
    private RetailOrderRepository retailOrderRepository;

    @InjectMocks
    private RetailOrderService retailOrderService;

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    @DisplayName("Get all product by status")
    void whenGettingAllProductOrderByStatusPremiumUserOrdersMustBePrioritizeAndInDescOrder_And_RegularUserOrdersMustBeInDescOrderJustAfterThePremiumUserOldOrder(String orderStatus) {
        // Mock data
        User seller = User.builder()
                .retailProducts(new ArrayList<>())
                .build();

        RetailOrder regularUserOldOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(null)
                        .build())
                .build();

        RetailOrder regularUserNewOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(null)
                        .build())
                .build();

        RetailOrder premiumUserOldOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(Premium.builder()
                                .registrationDate(LocalDateTime.now())
                                .build())
                        .build())
                .build();

        RetailOrder premiumUserNewOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .purchaser(User.builder()
                        .premium(Premium.builder()
                                .registrationDate(LocalDateTime.now())
                                .build())
                        .build())
                .build();

        RetailProduct activeRetailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(Arrays.asList(regularUserOldOrder, regularUserNewOrder, premiumUserOldOrder, premiumUserNewOrder))
                .status(Product.Status.ACTIVE)
                .build();

        RetailProduct inActiveRetailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(Arrays.asList(regularUserOldOrder, regularUserNewOrder, premiumUserOldOrder, premiumUserNewOrder))
                .status(Product.Status.INACTIVE)
                .build();

        seller.getRetailProducts().add(activeRetailProduct);
        seller.getRetailProducts().add(inActiveRetailProduct);

        // Calling the method
        List<RetailOrder> actualRetailOrders = retailOrderService.getAllProductOrderByStatus(seller, Order.Status.valueOf(orderStatus));
        List<RetailOrder> expectedRetailOrders = Arrays.asList(premiumUserNewOrder, premiumUserOldOrder, regularUserNewOrder, regularUserOldOrder);

        // Assertions
        assertIterableEquals(expectedRetailOrders, actualRetailOrders);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    @DisplayName("Get all ordered product by status")
    void shouldOnlyGetTheOrdersWithActiveProduct_And_MustBeInDescOrder(String orderStatus) {
        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailOrder activeOldRetailOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .retailProduct(RetailProduct.retailProductBuilder()
                        .status(Product.Status.ACTIVE)
                        .build())
                .build();

        RetailOrder activeNewRetailOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .retailProduct(RetailProduct.retailProductBuilder()
                        .status(Product.Status.ACTIVE)
                        .build())
                .build();

        RetailOrder InActiveActiveOldRetailOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now())
                .status(valueOf(orderStatus))
                .retailProduct(RetailProduct.retailProductBuilder()
                        .status(Product.Status.INACTIVE)
                        .build())
                .build();

        RetailOrder InActiveActiveNewRetailOrder = RetailOrder.retailOrderBuilder()
                .orderDate(LocalDateTime.now().plusDays(1))
                .status(valueOf(orderStatus))
                .retailProduct(RetailProduct.retailProductBuilder()
                        .status(Product.Status.INACTIVE)
                        .build())
                .build();

        user.getRetailOrders().add(activeOldRetailOrder);
        user.getRetailOrders().add(activeNewRetailOrder);
        user.getRetailOrders().add(InActiveActiveOldRetailOrder);
        user.getRetailOrders().add(InActiveActiveNewRetailOrder);

        // Calling the method
        List<RetailOrder> actualRetailOrders = retailOrderService.getAllOrderedProductsByStatus(user, valueOf(orderStatus));
        List<RetailOrder> expectedRetailOrders = Arrays.asList(activeNewRetailOrder, activeOldRetailOrder);

        // Assertions
        assertIterableEquals(expectedRetailOrders, actualRetailOrders);

        // Behavior verification
    }
}