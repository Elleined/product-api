package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product.State;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetailProductServiceImplTest {

    @Mock
    private RetailProductRepository retailProductRepository;
    @Mock
    private RetailOrderRepository retailOrderRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private RetailProductServiceImpl retailProductService;

    public void s() {

    }
    @Test
    void getAllExcept() {
        // Mock data
        User currentUser = User.builder()
                .retailProducts(new ArrayList<>())
                .build();

        // Adding 3 retail product for currentUser which will not be included in result
        currentUser.getRetailProducts().add(getMockRetailProduct());
        currentUser.getRetailProducts().add(getMockRetailProduct());
        currentUser.getRetailProducts().add(getMockRetailProduct());

        RetailProduct regularUserProduct = getMockRetailProduct();
        User regularUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .retailProducts(new ArrayList<>())
                .build();
        regularUser.getRetailProducts().add(regularUserProduct);

        RetailProduct premiumUserProduct = getMockRetailProduct();
        User premiumUser = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .retailProducts(new ArrayList<>())
                .build();
        premiumUser.getRetailProducts().add(premiumUserProduct);

        Premium premium = Premium.builder()
                .user(premiumUser)
                .build();

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(Arrays.asList(premium));
        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser));

        // Expected/ Actual values
        List<RetailProduct> expected = Arrays.asList(premiumUserProduct, regularUserProduct);

        // Calling the method
        List<RetailProduct> actual = retailProductService.getAllExcept(currentUser);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "LISTING", "SOLD", "REJECTED", "EXPIRED"})
    void getAllByState(String productState) {
        // Mock data
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .build();

        RetailProduct activeOldProduct = RetailProduct.retailProductBuilder()
                .status(ACTIVE)
                .state(State.valueOf(productState))
                .listingDate(LocalDateTime.now())
                .build();

        RetailProduct activeNewProduct = RetailProduct.retailProductBuilder()
                .status(ACTIVE)
                .state(State.valueOf(productState))
                .listingDate(LocalDateTime.now().plusDays(1))
                .build();

        RetailProduct inActiveProduct = RetailProduct.retailProductBuilder()
                .status(INACTIVE)
                .build();

        user.getRetailProducts().add(activeOldProduct);
        user.getRetailProducts().add(activeNewProduct);
        user.getRetailProducts().add(inActiveProduct);
        // Stubbing methods

        // Expected/ Actual values
        List<RetailProduct> expected = Arrays.asList(activeNewProduct, activeOldProduct);

        // Calling the method
        List<RetailProduct> actual = retailProductService.getAllByState(user, State.valueOf(productState));

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "LISTING", "SOLD", "REJECTED"})
    void deleteExpiredProducts(String notExpiredProductState) {
        // Mock data
        RetailProduct expiredProduct = RetailProduct.retailProductBuilder()
                .state(State.EXPIRED)
                .status(ACTIVE)
                .retailOrders(new ArrayList<>())
                .expirationDate(LocalDate.now().minusMonths(1))
                .build();

        RetailProduct notExpiredProduct = RetailProduct.retailProductBuilder()
                .state(State.valueOf(notExpiredProductState))
                .status(ACTIVE)
                .retailOrders(new ArrayList<>())
                .expirationDate(LocalDate.now().plusMonths(1))
                .build();

        List<RetailProduct> retailProducts = Arrays.asList(expiredProduct, notExpiredProduct);

        // Stubbing methods
        when(retailProductRepository.findAll()).thenReturn(retailProducts);

        // Expected/ Actual values

        // Calling the method
        retailProductService.deleteExpiredProducts();

        // Assertions
        assertEquals(State.EXPIRED, expiredProduct.getState());
        assertEquals(State.valueOf(notExpiredProductState), notExpiredProduct.getState());

        // Behavior verification
        verify(retailProductRepository).findAll();
        verify(retailProductRepository).saveAll(anyList());
        verify(retailOrderRepository, atMost(2)).saveAll(anyList());
    }

    @Test
    void getByDateRange() {
        // Mock data
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .build();

        RetailProduct inRanged = RetailProduct.retailProductBuilder()
                .listingDate(LocalDateTime.now())
                .build();

        RetailProduct outOfRange = RetailProduct.retailProductBuilder()
                .listingDate(LocalDateTime.now().minusMonths(1))
                .build();

        user.getRetailProducts().add(inRanged);
        user.getRetailProducts().add(outOfRange);

        LocalDateTime start = LocalDateTime.now().minusDays(1); // previous day
        // So we will fetch the retail product between previous day to tomorrow only.
        LocalDateTime end = LocalDateTime.now().plusDays(1); // tomorrow
        // Stubbing methods

        // Expected/ Actual values
        List<RetailProduct> expected = List.of(inRanged);

        // Calling the method
        List<RetailProduct> actual = retailProductService.getByDateRange(user,  start, end);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "SOLD", "REJECTED"})
    void updateAllPendingAndAcceptedOrders(String wantedOrderStatus) {
        // Mock data
        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailOrder pendingOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.PENDING)
                .build();

        RetailOrder acceptedOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.ACCEPTED)
                .build();

        List<RetailOrder> retailOrders = Arrays.asList(pendingOrder, acceptedOrder);
        retailProduct.getRetailOrders().addAll(retailOrders);

        // Stubbing methods
        when(retailOrderRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Expected/ Actual values

        // Calling the method
        retailProductService.updateAllPendingAndAcceptedOrders(retailProduct, Order.Status.valueOf(wantedOrderStatus));

        // Assertions
        assertNotNull(pendingOrder.getUpdatedAt());
        assertEquals(Order.Status.valueOf(wantedOrderStatus), pendingOrder.getStatus());

        assertNotNull(acceptedOrder.getUpdatedAt());
        assertEquals(Order.Status.valueOf(wantedOrderStatus), acceptedOrder.getStatus());

        // Behavior verification
        verify(retailProductRepository, atMost(2)).saveAll(anyList());
    }

    @Test
    void isRejectedBySeller() {
        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailProduct retailProduct = new RetailProduct();

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .updatedAt(LocalDateTime.now())
                .status(Order.Status.REJECTED)
                .build();

        user.getRetailOrders().add(retailOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertTrue(retailProductService.isRejectedBySeller(user, retailProduct), "Failing because provided time and status must be result with rejected order logics");

        // Behavior verification
    }

    @ParameterizedTest
    @DisplayName("is rejected by seller different scenario 1: with different order status")
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "SOLD"})
    void isRejectedBySellerWithDifferentOrderStatus(String orderStatus) {
        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailProduct retailProduct = new RetailProduct();

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .updatedAt(LocalDateTime.now())
                .status(Order.Status.valueOf(orderStatus))
                .build();

        user.getRetailOrders().add(retailOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertFalse(retailProductService.isRejectedBySeller(user, retailProduct), "Failing because order status must only be rejected.");

        // Behavior verification
    }

    @Test
    @DisplayName("is rejected by seller different scenario 2: with different rejection date")
    void isRejectedBySellerWithUpdatedAtDateThatCanBeOrderedAgain() {
        // Mock data
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailProduct retailProduct = new RetailProduct();

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .updatedAt(LocalDateTime.now().minusDays(2))
                .status(Order.Status.REJECTED)
                .build();

        user.getRetailOrders().add(retailOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertFalse(retailProductService.isRejectedBySeller(user, retailProduct), "Failing because updated at should be in the past.");

        // Behavior verification
    }

    @Test
    void calculateOrderPrice() {
        // Mock data
        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .pricePerUnit(10)
                .build();

        // Stubbing methods

        // Expected/ Actual values
        int expectedOrderPrice = 20;

        // Calling the method
        double actualOrderPrice = retailProductService.calculateOrderPrice(retailProduct, 2);

        // Assertions
        assertEquals(expectedOrderPrice, actualOrderPrice);

        // Behavior verification
    }

    @Test
    void calculateTotalPrice() {
        // Mock data
        int pricePerUnit = 50;
        int quantityPerUnit = 5;
        int availableQuantity = 10;

        // Stubbing methods

        // Expected/ Actual values
        int expectedTotalPrice = 100;

        // Calling the method
        double actualTotalPrice = retailProductService.calculateTotalPrice(pricePerUnit, quantityPerUnit, availableQuantity);
        // Assertions
        assertEquals(expectedTotalPrice, actualTotalPrice);

        // Behavior verification
    }

    @Test
    void calculateTotalPriceOfRetailProduct() {
        // Mock data

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .pricePerUnit(50)
                .quantityPerUnit(5)
                .availableQuantity(10)
                .build();

        // Stubbing methods

        // Expected/ Actual values
        int expectedTotalPrice = 100;

        // Calling the method
        double actualTotalPrice = retailProductService.calculateTotalPrice(retailProduct);
        // Assertions
        assertEquals(expectedTotalPrice, actualTotalPrice);

        // Behavior verification
    }

    private RetailProduct getMockRetailProduct() {
        return RetailProduct.retailProductBuilder()
                .state(State.LISTING)
                .status(ACTIVE)
                .build();
    }
}