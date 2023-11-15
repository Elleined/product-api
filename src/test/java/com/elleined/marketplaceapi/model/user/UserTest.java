package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    }

    @Test
    void hasNotBeenRejected() {
    }

    @Test
    void getFullName() {
    }

    @Test
    void notHave() {
    }

    @Test
    void isBalanceNotEnough() {
    }

    @Test
    void testHasOrder1() {
    }

    @Test
    void testHasOrder2() {
    }
}