package com.elleined.marketplaceapi.service.user.buyer;

import com.elleined.marketplaceapi.dto.order.RetailOrderDTO;
import com.elleined.marketplaceapi.dto.order.WholeSaleOrderDTO;
import com.elleined.marketplaceapi.exception.order.OrderAlreadyAcceptedException;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.order.OrderReachedCancellingTimeLimitException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.product.ProductRejectedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.NotOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.order.RetailOrderMapper;
import com.elleined.marketplaceapi.mapper.order.WholeSaleOrderMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuyerServiceImplTest {
    @Mock
    private RetailProductService retailProductService;
    @Mock
    private WholeSaleProductService wholeSaleProductService;
    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Mock
    private RetailOrderRepository retailOrderRepository;
    @Mock
    private RetailOrderMapper retailOrderMapper;
    @Mock
    private WholeSaleOrderMapper wholeSaleOrderMapper;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private BuyerServiceImpl buyerService;

    @Test
    void order() {
        // Mock data
        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());

        RetailOrderDTO retailOrderDTO = RetailOrderDTO.retailOrderDTOBuilder()

                .build();

        RetailProduct retailProduct = spy(RetailProduct.class);

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()

                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()

                .build();

        double expectedOrderPrice = 10;
        // Stubbing methods
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExceedingToAvailableQuantity(anyInt());

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(retailProductService.isRejectedBySeller(any(User.class), any(RetailProduct.class))).thenReturn(false);
        when(retailProductService.calculateOrderPrice(any(RetailProduct.class), anyInt())).thenReturn(expectedOrderPrice);
        when(addressService.getDeliveryAddressById(any(User.class), anyInt())).thenReturn(deliveryAddress);
        when(retailOrderMapper.toEntity(any(RetailOrderDTO.class), any(User.class), any(DeliveryAddress.class), anyDouble(), any(RetailProduct.class))).thenReturn(retailOrder);
        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(retailOrder);

        // Expected/ Actual values

        // Calling the method
        buyerService.order(user, retailOrderDTO);

        // Assertions
        assertTrue(user.getRetailOrders().contains(retailOrder));

        // Behavior verification
        verify(retailProductService).getById(anyInt());
        verify(retailProductService).isRejectedBySeller(any(User.class), any(RetailProduct.class));
        verify(retailProductService).calculateOrderPrice(any(RetailProduct.class), anyInt());
        verify(addressService).getDeliveryAddressById(any(User.class), anyInt());
        verify(retailOrderMapper).toEntity(any(RetailOrderDTO.class), any(User.class), any(DeliveryAddress.class), anyDouble(), any(RetailProduct.class));
        verify(retailOrderRepository).save(any(RetailOrder.class));
    }

    @Test
    @DisplayName("order product scenario: product is on sale")
    void orderProductThatIsOnSale() {
        // Mock data
        double expectedSalePrice = 100;
        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());

        RetailOrderDTO retailOrderDTO = RetailOrderDTO.retailOrderDTOBuilder()

                .build();

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setSaleRetailProduct(new SaleRetailProduct());

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()

                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()

                .build();

        // Stubbing methods
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExceedingToAvailableQuantity(anyInt());
        doReturn(true).when(retailProduct).isSale();

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(retailProductService.isRejectedBySeller(any(User.class), any(RetailProduct.class))).thenReturn(false);
        when(retailProductService.calculateOrderPrice(any(SaleRetailProduct.class), anyInt())).thenReturn(expectedSalePrice);
        when(addressService.getDeliveryAddressById(any(User.class), anyInt())).thenReturn(deliveryAddress);
        when(retailOrderMapper.toEntity(any(RetailOrderDTO.class), any(User.class), any(DeliveryAddress.class), anyDouble(), any(RetailProduct.class))).thenAnswer(i -> {
            retailOrder.setPrice(expectedSalePrice);
            return retailOrder;
        });
        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(retailOrder);

        // Expected/ Actual values

        // Calling the method
        buyerService.order(user, retailOrderDTO);

        // Assertions
        assertTrue(user.getRetailOrders().contains(retailOrder));
        assertEquals(expectedSalePrice, retailOrder.getPrice());

        // Behavior verification
        verify(retailProductService).getById(anyInt());
        verify(retailProductService).isRejectedBySeller(any(User.class), any(RetailProduct.class));
        verify(retailProductService).calculateOrderPrice(any(SaleRetailProduct.class), anyInt());
        verify(addressService).getDeliveryAddressById(any(User.class), anyInt());
        verify(retailOrderMapper).toEntity(any(RetailOrderDTO.class), any(User.class), any(DeliveryAddress.class), anyDouble(), any(RetailProduct.class));
        verify(retailOrderRepository).save(any(RetailOrder.class));
    }

    @Test
    void WholeSaleOrder() {
        // Mock data
        User user = spy(User.class);
        user.setWholeSaleOrders(new ArrayList<>());

        WholeSaleOrderDTO wholeSaleOrderDTO = WholeSaleOrderDTO.wholeSaleOrderDTOBuilder()

                .build();

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()

                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()

                .build();

        double expectedOrderPrice = 10;
        // Stubbing methods
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(true).when(wholeSaleProduct).isListed();
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));

        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        when(wholeSaleProductService.isRejectedBySeller(any(User.class), any(WholeSaleProduct.class))).thenReturn(false);
        when(addressService.getDeliveryAddressById(any(User.class), anyInt())).thenReturn(deliveryAddress);
        when(wholeSaleOrderMapper.toEntity(any(WholeSaleOrderDTO.class), any(User.class), any(DeliveryAddress.class), any(WholeSaleProduct.class))).thenReturn(wholeSaleOrder);
        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(wholeSaleOrder);

        // Expected/ Actual values

        // Calling the method
        buyerService.order(user, wholeSaleOrderDTO);

        // Assertions
        assertTrue(user.getWholeSaleOrders().contains(wholeSaleOrder));

        // Behavior verification
        verify(wholeSaleProductService).getById(anyInt());
        verify(wholeSaleProductService).isRejectedBySeller(any(User.class), any(WholeSaleProduct.class));
        verify(addressService).getDeliveryAddressById(any(User.class), anyInt());
        verify(wholeSaleOrderMapper).toEntity(any(WholeSaleOrderDTO.class), any(User.class), any(DeliveryAddress.class), any(WholeSaleProduct.class));
        verify(wholeSaleOrderRepository).save(any(WholeSaleOrder.class));
    }

    @Test
    void orderWholeSaleProductThatIsOnSale() {
        // Mock data
        User user = spy(User.class);
        user.setWholeSaleOrders(new ArrayList<>());

        WholeSaleOrderDTO wholeSaleOrderDTO = WholeSaleOrderDTO.wholeSaleOrderDTOBuilder()

                .build();

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setSaleWholeSaleProduct(SaleWholeSaleProduct.saleWholeSaleProductBuilder()
                .salePrice(100)
                .build());

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()

                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()

                .build();

        double expectedOrderPrice = 10;
        // Stubbing methods
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(true).when(wholeSaleProduct).isListed();
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(true).when(wholeSaleProduct).isSale();

        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        when(wholeSaleProductService.isRejectedBySeller(any(User.class), any(WholeSaleProduct.class))).thenReturn(false);
        when(addressService.getDeliveryAddressById(any(User.class), anyInt())).thenReturn(deliveryAddress);
        when(wholeSaleOrderMapper.toEntity(any(WholeSaleOrderDTO.class), any(User.class), any(DeliveryAddress.class), any(WholeSaleProduct.class))).thenReturn(wholeSaleOrder);
        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(wholeSaleOrder);

        // Expected/ Actual values

        // Calling the method
        buyerService.order(user, wholeSaleOrderDTO);

        // Assertions
        assertTrue(user.getWholeSaleOrders().contains(wholeSaleOrder));
        assertEquals(wholeSaleProduct.getSaleWholeSaleProduct().getSalePrice(), wholeSaleOrder.getPrice());

        // Behavior verification
        verify(wholeSaleProductService).getById(anyInt());
        verify(wholeSaleProductService).isRejectedBySeller(any(User.class), any(WholeSaleProduct.class));
        verify(addressService).getDeliveryAddressById(any(User.class), anyInt());
        verify(wholeSaleOrderMapper).toEntity(any(WholeSaleOrderDTO.class), any(User.class), any(DeliveryAddress.class), any(WholeSaleProduct.class));
        verify(wholeSaleOrderRepository).save(any(WholeSaleOrder.class));
    }

    @Test
    @DisplayName("order product validation 1: cannot order a product that is rejected")
    void cannotOrderProductThatIsRejected() {
        // Expected/ Actual values

        // Mock data
        User user = new User();
        RetailProduct retailProduct = new RetailProduct();
        retailProduct.setState(Product.State.REJECTED);

        WholeSaleProduct wholeSaleProduct = new WholeSaleProduct();
        wholeSaleProduct.setState(Product.State.REJECTED);
        // Stubbing methods

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(ProductRejectedException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(ProductRejectedException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "ACCEPTED"})
    @DisplayName("order product validation 2 and 3: cannot order a product that already have pending or accepted order to that product")
    void cannotOrderProductThatHasAlreadyHavePendingOrAcceptedOrder(String orderStatus) {
        // Expected/ Actual values

        // Mock data
        User user = new User();
        user.setRetailOrders(new ArrayList<>());
        user.setWholeSaleOrders(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .status(Order.Status.valueOf(orderStatus))
                .build();

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .status(Order.Status.valueOf(orderStatus))
                .build();

        user.getRetailOrders().add(retailOrder);
        user.getWholeSaleOrders().add(wholeSaleOrder);


        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrows(ProductOrderException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrows(ProductOrderException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("order product validation 4: cannot order a product that already been deleted")
    void cannotOrderProductThatIsAlreadyBeenDeleted() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setStatus(Product.Status.INACTIVE);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setStatus(Product.Status.INACTIVE);

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(ResourceNotFoundException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(ResourceNotFoundException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("order product validation 5: cannot order a product that already been sold")
    void cannotOrderProductThatIsAlreadyBeenSold() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setState(Product.State.SOLD);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setState(Product.State.SOLD);

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isDeleted();

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(ProductAlreadySoldException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(ProductAlreadySoldException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    @DisplayName("order product validation 6: cannot order a product that is not listed")
    void cannotOrderProductThatIsNotListed(String notListedProductState) {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setState(Product.State.valueOf(notListedProductState));

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setState(Product.State.valueOf(notListedProductState));

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(false).when(wholeSaleProduct).isSold();

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(ProductNotListedException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(ProductNotListedException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("order product validation 7: cannot order a product he owns")
    void cannotOrderProductThatHeOwns() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);
        user.setRetailProducts(new ArrayList<>());
        user.setWholeSaleProducts(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        user.getRetailProducts().add(retailProduct);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        user.getWholeSaleProducts().add(wholeSaleProduct);

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(true).when(wholeSaleProduct).isListed();

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(ResourceOwnedException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(ResourceOwnedException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService, wholeSaleProductService);
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("order product validation 8: cannot order a product if seller already rejected the buyer. can only re-order again after 1 day")
    void cannotOrderProductThatSellerRecentlyRejectedMustWait1DayBeforeBuyerToReOrderAgain() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(true).when(wholeSaleProduct).isListed();
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        when(retailProductService.isRejectedBySeller(any(User.class), any(RetailProduct.class))).thenReturn(true);
        when(wholeSaleProductService.isRejectedBySeller(any(User.class), any(WholeSaleProduct.class))).thenReturn(true);

        // Calling the method
        // Assertions
        assertThrowsExactly(BuyerAlreadyRejectedException.class, () -> buyerService.order(user, new RetailOrderDTO()));
        assertThrowsExactly(BuyerAlreadyRejectedException.class, () -> buyerService.order(user, new WholeSaleOrderDTO()));

        // Behavior verification
        verifyNoInteractions(addressService, retailOrderMapper, wholeSaleOrderMapper, retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("order product validation 9: cannot order a product thats exceeds to available quantity")
    void cannotOrderProductThatExceedsToAvailableQuantity() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setAvailableQuantity(100);

        RetailOrderDTO retailOrderDTO = RetailOrderDTO.retailOrderDTOBuilder()
                .orderQuantity(9_999)
                .build();

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(retailProductService.isRejectedBySeller(any(User.class), any(RetailProduct.class))).thenReturn(false);

        // Calling the method
        // Assertions
        assertThrowsExactly(OrderQuantiantyExceedsException.class, () -> buyerService.order(user, retailOrderDTO));

        // Behavior verification
        verifyNoInteractions(addressService, retailOrderMapper, retailOrderRepository);
    }

    @Test
    @DisplayName("order product validation 10: cannot order a product that is expired")
    void cannotOrderProductThatIsExpired() {
        // Expected/ Actual values

        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setState(Product.State.EXPIRED);
        retailProduct.setExpirationDate(LocalDate.now().minusMonths(1));

        // Stubbing methods
        doReturn(false).when(retailProduct).isRejected();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExceedingToAvailableQuantity(anyInt());

        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        when(retailProductService.isRejectedBySeller(any(User.class), any(RetailProduct.class))).thenReturn(false);

        // Calling the method
        // Assertions
        assertThrowsExactly(ProductExpiredException.class, () -> buyerService.order(user, new RetailOrderDTO()));

        // Behavior verification
        verifyNoInteractions(addressService, retailOrderMapper, retailOrderRepository);
    }




    @Test
    void cancelOrder() {
        // Expected/ Actual values
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // Mock data
        User user = spy(User.class);
        RetailOrder retailOrder = spy(RetailOrder.class);
        retailOrder.setRetailProduct(RetailProduct.retailProductBuilder()
                .id(1)
                .build());
        retailOrder.setUpdatedAt(expectedUpdatedAt);

        // Stubbing methods
        doReturn(true).when(user).hasOrder(any(RetailOrder.class));
        doReturn(false).when(retailOrder).isAccepted();
        doReturn(false).when(retailOrder).reachedCancellingTimeLimit();

        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(retailOrder);

        // Calling the method
        buyerService.cancelOrder(user, retailOrder);

        // Assertions
        assertEquals(Order.Status.CANCELLED, retailOrder.getStatus());
        assertNotEquals(expectedUpdatedAt, retailOrder.getUpdatedAt());

        // Behavior verification
        verify(retailOrderRepository).save(any(RetailOrder.class));
    }

    @Test
    void WholeSaleCancelOrder() {
        // Expected/ Actual values
        LocalDateTime expectedUpdatedAt = LocalDateTime.now();

        // Mock data
        User user = spy(User.class);
        WholeSaleOrder wholeSaleOrder = spy(WholeSaleOrder.class);
        wholeSaleOrder.setWholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                .id(1)
                .build());
        wholeSaleOrder.setUpdatedAt(expectedUpdatedAt);

        // Stubbing methods
        doReturn(true).when(user).hasOrder(any(WholeSaleOrder.class));
        doReturn(false).when(wholeSaleOrder).isAccepted();
        doReturn(false).when(wholeSaleOrder).reachedCancellingTimeLimit();

        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(wholeSaleOrder);

        // Calling the method
        buyerService.cancelOrder(user, wholeSaleOrder);

        // Assertions
        assertEquals(Order.Status.CANCELLED, wholeSaleOrder.getStatus());
        assertNotEquals(expectedUpdatedAt, wholeSaleOrder.getUpdatedAt());

        // Behavior verification
        verify(wholeSaleOrderRepository).save(any(WholeSaleOrder.class));
    }

    @Test
    @DisplayName("cancel order validation 1: user cannot cancel an order he doesn't own")
    void cannotCancelOrderWhenUserTryingToCancelAnOrderThatHeDoesNotOwn() {
        // Mock data
        RetailOrder retailOrder = new RetailOrder();
        WholeSaleOrder wholeSaleOrder = new WholeSaleOrder();

        User user = User.builder()
                .retailOrders(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .build();
        // user.getRetailOrders().add(retailOrder);

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(NotOwnedException.class, () -> buyerService.cancelOrder(user, retailOrder));
        assertThrowsExactly(NotOwnedException.class, () -> buyerService.cancelOrder(user, wholeSaleOrder));

        // Behavior verification
        verifyNoInteractions(retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("cancel order validation 2: user cannot cancel an order when seller already accepted the order")
    void cannotCancelOrderWhenOrderHasAlreadyBeenAcceptedByTheSeller() {
        // Mock data
        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .status(Order.Status.ACCEPTED)
                .build();

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .status(Order.Status.ACCEPTED)
                .build();

        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());
        user.setWholeSaleOrders(new ArrayList<>());

        // user.getRetailOrders().add(retailOrder);

        // Stubbing methods
        doReturn(true).when(user).hasOrder(any(RetailOrder.class));
        doReturn(true).when(user).hasOrder(any(WholeSaleOrder.class));

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(OrderAlreadyAcceptedException.class, () -> buyerService.cancelOrder(user, retailOrder));
        assertThrowsExactly(OrderAlreadyAcceptedException.class, () -> buyerService.cancelOrder(user, wholeSaleOrder));

        // Behavior verification
        verifyNoInteractions(retailOrderRepository, wholeSaleOrderRepository);
    }

    @Test
    @DisplayName("cancel order validation 3: user cannot cancel an order's.yml order is more than 1 day after the order date")
    void cannotCancelOrderWhenOrderIsMoreThan1DayAfterTheOrderDAte() {
        // Mock data
        RetailOrder retailOrder = spy(RetailOrder.class);
        retailOrder.setUpdatedAt(LocalDateTime.now().minusDays(1));

        WholeSaleOrder wholeSaleOrder = spy(WholeSaleOrder.class);
        wholeSaleOrder.setUpdatedAt(LocalDateTime.now().minusDays(1));

        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());
        user.setWholeSaleOrders(new ArrayList<>());

        // user.getRetailOrders().add(retailOrder);

        // Stubbing methods
        doReturn(true).when(user).hasOrder(any(RetailOrder.class));
        doReturn(true).when(user).hasOrder(any(WholeSaleOrder.class));
        doReturn(false).when(retailOrder).isAccepted();
        doReturn(false).when(wholeSaleOrder).isAccepted();

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(OrderReachedCancellingTimeLimitException.class, () -> buyerService.cancelOrder(user, retailOrder));
        assertThrowsExactly(OrderReachedCancellingTimeLimitException.class, () -> buyerService.cancelOrder(user, wholeSaleOrder));

        // Behavior verification
        verifyNoInteractions(retailOrderRepository, wholeSaleOrderRepository);
    }
}