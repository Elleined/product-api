package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.elleined.marketplaceapi.model.order.Order.Status.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    @Test
    void hasVerificationStatusOf() {
        User verifiedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .build())
                .build();

        User notVerifiedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .build())
                .build();

        assertTrue(verifiedUser.isVerified());
        assertTrue(notVerifiedUser.isNotVerified());
    }

    @Test
    void hasShopRegistration() {
        User user = User.builder()
                .shop(Shop.builder().build())
                .build();

        assertTrue(user.hasShopRegistration());
    }

    @Test
    void isProductAlreadyInCart() {
        User user = User.builder()
                .retailCartItems(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        RetailProduct retailProduct = new RetailProduct();
        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();
        user.getRetailCartItems().add(retailCartItem);

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();
        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();
        user.getWholeSaleCartItems().add(wholeSaleCartItem);

        assertTrue(user.isProductAlreadyInCart(retailProduct));
        assertTrue(user.isProductAlreadyInCart(wholeSaleProduct));
    }

    @Test
    void hasProduct() {
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .wholeSaleProducts(new ArrayList<>())
                .build();
        RetailProduct retailProduct = new RetailProduct();
        user.getRetailProducts().add(retailProduct);

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();
        user.getWholeSaleProducts().add(wholeSaleProduct);

        assertTrue(user.hasProduct(retailProduct));
        assertTrue(user.hasProduct(wholeSaleProduct));
    }

    @Test
    void hasOrder() {
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .build();
        RetailOrder retailOrder = new RetailOrder();
        user.getRetailOrders().add(retailOrder);

        WholeSaleOrder wholeSaleOrder = new WholeSaleOrder();
        user.getWholeSaleOrders().add(wholeSaleOrder);

        assertTrue(user.hasOrder(retailOrder));
        assertTrue(user.hasOrder(wholeSaleOrder));
    }

    @Test
    void hasSellableProductOrder() {
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .wholeSaleProducts(new ArrayList<>())
                .build();

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(new ArrayList<>())
                .build();
        user.getRetailProducts().add(retailProduct);

        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(new ArrayList<>())
                .build();
        user.getWholeSaleProducts().add(wholeSaleProduct);

        RetailOrder retailOrder = new RetailOrder();
        retailProduct.getRetailOrders().add(retailOrder);

        WholeSaleOrder wholeSaleOrder = new WholeSaleOrder();
        wholeSaleProduct.getWholeSaleOrders().add(wholeSaleOrder);

        assertTrue(user.hasSellableProductOrder(retailOrder));
        assertTrue(user.hasSellableProductOrder(wholeSaleOrder));
    }

    @Test
    void isPremiumAndNotExpired() {
        User user = User.builder()
                .premium(Premium.builder()
                        .registrationDate(LocalDateTime.now().minusMonths(1))
                        .build())
                .build();

        assertTrue(user.isPremiumAndNotExpired());
    }

    @Test
    void isRejected() {
        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .validId(null)
                        .build())
                .build();

        assertTrue(user.isRejected());
    }

    @Test
    void hasNotBeenRejected() {
        User user = User.builder()
                .userVerification(UserVerification.builder()
                        .validId("valid id")
                        .build())
                .build();

        assertTrue(user.isNotRejected());
    }

    @Test
    void getFullName() {
        User user = User.builder()
                .userDetails(UserDetails.builder()
                        .firstName("first name")
                        .middleName("middle name")
                        .lastName("last name")
                        .build())
                .build();

        String expectedFullName = "first name middle name last name";
        assertEquals(expectedFullName, user.getFullName());
    }


    @Test
    void isBalanceNotEnough() {
        User user = User.builder()
                .balance(new BigDecimal(0))
                .build();

        BigDecimal liability = new BigDecimal(6_000);
        assertTrue(user.isBalanceNotEnough(liability));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    void isRetailProductHasRetailOrderWithStatusOf(String orderStatus) {
        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .build();

        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(new ArrayList<>())
                .build();

        Order.Status status = Order.Status.valueOf(orderStatus);
        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.valueOf(orderStatus))
                .retailProduct(retailProduct)
                .build();

        user.getRetailOrders().add(retailOrder);

        assertTrue(user.hasOrder(retailProduct, status));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CANCELLED", "PENDING", "ACCEPTED", "REJECTED", "SOLD"})
    void isWholeSaleProductHasWholeSaleOrderWithStatusOf(String orderStatus) {
        User user = User.builder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(new ArrayList<>())
                .build();

        Order.Status status = Order.Status.valueOf(orderStatus);
        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .status(status)
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        user.getWholeSaleOrders().add(wholeSaleOrder);

        assertTrue(user.hasOrder(wholeSaleProduct, status));
    }
}