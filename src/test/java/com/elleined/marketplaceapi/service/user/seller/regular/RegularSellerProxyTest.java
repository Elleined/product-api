package com.elleined.marketplaceapi.service.user.seller.regular;

import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.user.seller.SellerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegularSellerProxyTest {

    @Mock
    private  SellerService sellerService;
    @Mock
    private  WholeSaleProductService wholeSaleProductService;
    @Mock
    private  RetailProductService retailProductService;
    @Mock
    private  FeeService feeService;
    @InjectMocks
    private RegularSellerProxy regularSellerProxy;

    @Test
    void saleProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSaleProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void saveProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSaveProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void updateProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleUpdateProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void deleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleDeleteProduct() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void acceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void rejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void soldOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void wholeSaleSoldOrder() {
        // Expected values

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assestions

        // Behavior verification
    }

    @Test
    void isExceedsToMaxListingPerDay() {
        // Expected values

        // Mock Data
        List<RetailProduct> retailProducts = Arrays.asList(
                getMockRetailProduct(),
                getMockRetailProduct(),
                getMockRetailProduct(),
                getMockRetailProduct(),
                getMockRetailProduct()
        );
        List<WholeSaleProduct> wholeSaleProducts = Arrays.asList(
                getMockWholeSaleProduct(),
                getMockWholeSaleProduct(),
                getMockWholeSaleProduct(),
                getMockWholeSaleProduct(),
                getMockWholeSaleProduct()
        );
        User user = User.builder()
                .retailProducts(retailProducts)
                .wholeSaleProducts(wholeSaleProducts)
                .build();

        // Stubbing methods
        when(retailProductService.getByDateRange(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(retailProducts);
        when(wholeSaleProductService.getByDateRange(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(wholeSaleProducts);

        // Calling the method
        // Assestions
        assertTrue(regularSellerProxy.isExceedsToMaxListingPerDay(user));

        // Behavior verification
        verify(retailProductService).getByDateRange(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(wholeSaleProductService).getByDateRange(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void isExceedsToMaxRejectionPerDay() {
        // Expected values

        // Mock Data
        List<RetailOrder> retailOrders = Arrays.asList(
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED),
                getMockRetailOrder(Order.Status.REJECTED)
        );
        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED),
                getMockWholeSaleOrder(Order.Status.REJECTED)
        );
        User user = User.builder()
                .wholeSaleOrders(wholeSaleOrders)
                .retailOrders(retailOrders)
                .build();

        // Stubbing methods

        // Calling the method
        // Assestions
        assertTrue(regularSellerProxy.isExceedsToMaxRejectionPerDay(user), "Failing because the orders not exceeds to " + RegularSellerRestriction.MAX_ORDER_REJECTION_PER_DAY);

        // Behavior verification
    }

    @Test
    void isExceedsToMaxAcceptedOrder() {
        // Expected values

        // Mock Data
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .wholeSaleProducts(new ArrayList<>())
                .build();

        List<RetailOrder> retailOrders = Arrays.asList(
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED),
                getMockRetailOrder(Order.Status.ACCEPTED)
        );
        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .status(Product.Status.ACTIVE)
                .retailOrders(retailOrders)
                .build();

        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED),
                getMockWholeSaleOrder(Order.Status.ACCEPTED)
        );
        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(wholeSaleOrders)
                .status(Product.Status.ACTIVE)
                .build();

        RetailProduct inActiveRetailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(retailOrders)
                .status(Product.Status.INACTIVE)
                .build();

        WholeSaleProduct inActiveWholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(wholeSaleOrders)
                .status(Product.Status.INACTIVE)
                .build();

        user.getRetailProducts().add(retailProduct);
        user.getWholeSaleProducts().add(wholeSaleProduct);
        user.getRetailProducts().add(inActiveRetailProduct);
        user.getWholeSaleProducts().add(inActiveWholeSaleProduct);

        // Stubbing methods

        // Calling the method
        // Assestions
        assertTrue(regularSellerProxy.isExceedsToMaxAcceptedOrder(user), "Failing because total orders should exceeds to " + RegularSellerRestriction.MAX_ACCEPTED_ORDER);

        // Behavior verification
    }

    @Test
    void isExceedsToMaxPendingOrder() {
        // Expected values

        // Mock Data
        User user = User.builder()
                .retailProducts(new ArrayList<>())
                .wholeSaleProducts(new ArrayList<>())
                .build();

        List<RetailOrder> retailOrders = Arrays.asList(
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING),
                getMockRetailOrder(Order.Status.PENDING)
        );
        RetailProduct retailProduct = RetailProduct.retailProductBuilder()
                .status(Product.Status.ACTIVE)
                .retailOrders(retailOrders)
                .build();

        List<WholeSaleOrder> wholeSaleOrders = Arrays.asList(
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING),
                getMockWholeSaleOrder(Order.Status.PENDING)
        );
        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(wholeSaleOrders)
                .status(Product.Status.ACTIVE)
                .build();

        RetailProduct inActiveRetailProduct = RetailProduct.retailProductBuilder()
                .retailOrders(retailOrders)
                .status(Product.Status.INACTIVE)
                .build();

        WholeSaleProduct inActiveWholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .wholeSaleOrders(wholeSaleOrders)
                .status(Product.Status.INACTIVE)
                .build();

        user.getRetailProducts().add(retailProduct);
        user.getWholeSaleProducts().add(wholeSaleProduct);
        user.getRetailProducts().add(inActiveRetailProduct);
        user.getWholeSaleProducts().add(inActiveWholeSaleProduct);

        // Stubbing methods

        // Calling the method
        // Assestions
        assertTrue(regularSellerProxy.isExceedsToMaxPendingOrder(user), "Failing because total orders should exceeds to " + RegularSellerRestriction.MAX_PENDING_ORDER);

        // Behavior verification
    }

    private WholeSaleProduct getMockWholeSaleProduct() {
        return WholeSaleProduct.wholeSaleProductBuilder()
                .listingDate(LocalDateTime.now())
                .build();
    }

    private RetailProduct getMockRetailProduct() {
        return RetailProduct.retailProductBuilder()
                .listingDate(LocalDateTime.now())
                .build();
    }

    private RetailOrder getMockRetailOrder(Order.Status status) {
        return RetailOrder.retailOrderBuilder()
                .updatedAt(LocalDateTime.now())
                .status(status)
                .build();
    }

    private WholeSaleOrder getMockWholeSaleOrder(Order.Status status) {
        return WholeSaleOrder.wholeSaleOrderBuilder()
                .updatedAt(LocalDateTime.now())
                .status(status)
                .build();
    }
}