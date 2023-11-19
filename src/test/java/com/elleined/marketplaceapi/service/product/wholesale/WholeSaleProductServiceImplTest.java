package com.elleined.marketplaceapi.service.product.wholesale;

import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.*;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleProductServiceImplTest {
    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Mock
    private WholeSaleProductRepository wholeSaleProductRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private WholeSaleProductServiceImpl wholeSaleProductService;

    @Test
    void getAllExcept() {
        // Mock data
        User currentUser = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        // Adding 3 retail product for currentUser which will not be included in result
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());

        WholeSaleProduct regularUserProduct = getMockWholeSaleProduct();
        User regularUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        regularUser.getWholeSaleProducts().add(regularUserProduct);

        WholeSaleProduct premiumUserProduct = getMockWholeSaleProduct();
        User premiumUser = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        premiumUser.getWholeSaleProducts().add(premiumUserProduct);

        Premium premium = Premium.builder()
                .user(premiumUser)
                .build();

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(Arrays.asList(premium));
        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser));

        // Expected/ Actual values
        List<WholeSaleProduct> expected = Arrays.asList(premiumUserProduct, regularUserProduct);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getAllExcept(currentUser);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    @Test
    void getAllExceptWithVerifiedUsersOnly() {
        // Mock data
        User currentUser = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        // Adding 3 retail product for currentUser which will not be included in result
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());

        WholeSaleProduct regularUserProduct = getMockWholeSaleProduct();
        User regularUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        regularUser.getWholeSaleProducts().add(regularUserProduct);

        WholeSaleProduct premiumUserProduct = getMockWholeSaleProduct();
        User premiumUser = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        premiumUser.getWholeSaleProducts().add(premiumUserProduct);

        Premium premium = Premium.builder()
                .user(premiumUser)
                .build();

        User unverfiedUser = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .userVerification(new UserVerification())
                .build();
        unverfiedUser.getWholeSaleProducts().add(getMockWholeSaleProduct());

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(Arrays.asList(premium));
        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser, unverfiedUser));

        // Expected/ Actual values
        List<WholeSaleProduct> expected = Arrays.asList(premiumUserProduct, regularUserProduct);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getAllExcept(currentUser);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    @Test
    void getAllExceptWithVerifiedUsersAndHasShopRegistrationOnly() {
        // Mock data
        User currentUser = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        // Adding 3 retail product for currentUser which will not be included in result
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());

        WholeSaleProduct regularUserProduct = getMockWholeSaleProduct();
        User regularUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        regularUser.getWholeSaleProducts().add(regularUserProduct);

        WholeSaleProduct premiumUserProduct = getMockWholeSaleProduct();
        User premiumUser = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        premiumUser.getWholeSaleProducts().add(premiumUserProduct);

        Premium premium = Premium.builder()
                .user(premiumUser)
                .build();

        User verifiedUserButNoShop = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(null)
                .build();

        verifiedUserButNoShop.getWholeSaleProducts().add(getMockWholeSaleProduct());

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(Arrays.asList(premium));
        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser, verifiedUserButNoShop));

        // Expected/ Actual values
        List<WholeSaleProduct> expected = Arrays.asList(premiumUserProduct, regularUserProduct);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getAllExcept(currentUser);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    @Test
    void getAllExceptWithVerifiedUsersAndHasShopRegistrationAndActiveProductOnly() {
        // Mock data
        User currentUser = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        // Adding 3 retail product for currentUser which will not be included in result
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());
        currentUser.getWholeSaleProducts().add(getMockWholeSaleProduct());

        WholeSaleProduct regularUserProduct = getMockWholeSaleProduct();
        User regularUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        regularUser.getWholeSaleProducts().add(regularUserProduct);

        WholeSaleProduct premiumUserProduct = getMockWholeSaleProduct();
        User premiumUser = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now())
                        .build())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        premiumUser.getWholeSaleProducts().add(premiumUserProduct);

        Premium premium = Premium.builder()
                .user(premiumUser)
                .build();

        User verifiedUserWithShopButProductIsInactive = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .shop(new Shop())
                .build();

        WholeSaleProduct inActiveProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .status(INACTIVE)
                .build();

        verifiedUserWithShopButProductIsInactive.getWholeSaleProducts().add(inActiveProduct);

        // Stubbing methods
        when(premiumRepository.findAll()).thenReturn(Arrays.asList(premium));
        when(userRepository.findAll()).thenReturn(Arrays.asList(regularUser, verifiedUserWithShopButProductIsInactive));

        // Expected/ Actual values
        List<WholeSaleProduct> expected = Arrays.asList(premiumUserProduct, regularUserProduct);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getAllExcept(currentUser);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(premiumRepository).findAll();
        verify(userRepository).findAll();
    }

    private WholeSaleProduct getMockWholeSaleProduct() {
        return WholeSaleProduct.wholeSaleProductBuilder()
                .state(Product.State.LISTING)
                .status(ACTIVE)
                .build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "LISTING", "SOLD", "REJECTED", "EXPIRED"})
    void getAllByState(String productState) {
        // Mock data
        User user = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        WholeSaleProduct activeOldProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .status(ACTIVE)
                .state(Product.State.valueOf(productState))
                .listingDate(LocalDateTime.now())
                .build();

        WholeSaleProduct activeNewProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .status(ACTIVE)
                .state(Product.State.valueOf(productState))
                .listingDate(LocalDateTime.now().plusDays(1))
                .build();

        WholeSaleProduct inActiveProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .status(INACTIVE)
                .build();

        user.getWholeSaleProducts().add(activeOldProduct);
        user.getWholeSaleProducts().add(activeNewProduct);
        user.getWholeSaleProducts().add(inActiveProduct);
        // Stubbing methods

        // Expected/ Actual values
        List<WholeSaleProduct> expected = Arrays.asList(activeNewProduct, activeOldProduct);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getAllByState(user, Product.State.valueOf(productState));

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
    }

    @Test
    void getByDateRange() {
        // Mock data
        User user = User.builder()
                .wholeSaleProducts(new ArrayList<>())
                .build();

        WholeSaleProduct inRanged = WholeSaleProduct.wholeSaleProductBuilder()
                .listingDate(LocalDateTime.now())
                .build();

        WholeSaleProduct outOfRange = WholeSaleProduct.wholeSaleProductBuilder()
                .listingDate(LocalDateTime.now().minusMonths(1))
                .build();

        user.getWholeSaleProducts().add(inRanged);
        user.getWholeSaleProducts().add(outOfRange);

        LocalDateTime start = LocalDateTime.now().minusDays(1); // previous day
        // So we will fetch the retail product between previous day to tomorrow only.
        LocalDateTime end = LocalDateTime.now().plusDays(1); // tomorrow
        // Stubbing methods

        // Expected/ Actual values
        List<WholeSaleProduct> expected = List.of(inRanged);

        // Calling the method
        List<WholeSaleProduct> actual = wholeSaleProductService.getByDateRange(user,  start, end);

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "SOLD", "REJECTED"})
    void updateAllPendingAndAcceptedOrders(String wantedOrderStatus) {
        // Mock data
        WholeSaleProduct retailProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleOrder pendingOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .status(Order.Status.PENDING)
                .build();

        WholeSaleOrder acceptedOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .status(Order.Status.ACCEPTED)
                .build();

        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(pendingOrder, acceptedOrder);
        retailProduct.getWholeSaleOrders().addAll(wholeSaleOrders);

        // Stubbing methods
        when(wholeSaleOrderRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // Expected/ Actual values

        // Calling the method
        wholeSaleProductService.updateAllPendingAndAcceptedOrders(retailProduct, Order.Status.valueOf(wantedOrderStatus));

        // Assertions
        assertNotNull(pendingOrder.getUpdatedAt());
        assertEquals(Order.Status.valueOf(wantedOrderStatus), pendingOrder.getStatus());

        assertNotNull(acceptedOrder.getUpdatedAt());
        assertEquals(Order.Status.valueOf(wantedOrderStatus), acceptedOrder.getStatus());

        // Behavior verification
        verify(wholeSaleProductRepository, atMost(2)).saveAll(anyList());
    }

    @Test
    void isRejectedBySeller() {
        // Mock data
        User user = User.builder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .updatedAt(LocalDateTime.now())
                .status(Order.Status.REJECTED)
                .build();

        user.getWholeSaleOrders().add(wholeSaleOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertTrue(wholeSaleProductService.isRejectedBySeller(user, wholeSaleProduct), "Failing because provided time and status must be result with rejected order logics");

        // Behavior verification
    }

    @ParameterizedTest
    @DisplayName("is rejected by seller different scenario 1: with different order status")
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "SOLD"})
    void isRejectedBySellerWithDifferentOrderStatus(String orderStatus) {
        // Mock data
        User user = User.builder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .updatedAt(LocalDateTime.now())
                .status(Order.Status.valueOf(orderStatus))
                .build();

        user.getWholeSaleOrders().add(wholeSaleOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertFalse(wholeSaleProductService.isRejectedBySeller(user, wholeSaleProduct), "Failing because order status must only be rejected.");

        // Behavior verification
    }

    @Test
    @DisplayName("is rejected by seller different scenario 2: with different rejection date")
    void isRejectedBySellerWithUpdatedAtDateThatCanBeOrderedAgain() {
        // Mock data
        User user = User.builder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .updatedAt(LocalDateTime.now().minusDays(2))
                .status(Order.Status.REJECTED)
                .build();

        user.getWholeSaleOrders().add(wholeSaleOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertFalse(wholeSaleProductService.isRejectedBySeller(user, wholeSaleProduct), "Failing because updated at should be in the past.");

        // Behavior verification
    }
}