package com.elleined.marketplaceapi.service.cart.retail;

import com.elleined.marketplaceapi.dto.cart.RetailCartItemDTO;
import com.elleined.marketplaceapi.exception.order.OrderQuantiantyExceedsException;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductExpiredException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.RetailCartItemMapper;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.RetailCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.Product.State;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.cart.RetailCartItemRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
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
import java.util.Collections;
import java.util.List;

import static com.elleined.marketplaceapi.model.order.Order.Status.REJECTED;
import static com.elleined.marketplaceapi.model.product.Product.State.SOLD;
import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
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

    @Mock
    private AddressService addressService;

    @InjectMocks
    private RetailCartItemServiceImpl retailCartItemService;

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    void getAll(String productStatus) {
        User user = new User();

        RetailCartItem activeAndListed = RetailCartItem.retailCartItemBuilder()
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .status(Product.Status.ACTIVE)
                        .state(State.LISTING)
                        .build())
                .build();

        RetailCartItem activeAndNotListed = RetailCartItem.retailCartItemBuilder()
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .state(State.valueOf(productStatus))
                        .status(Product.Status.ACTIVE)
                        .build())
                .build();

        RetailCartItem inActiveAndNotListed = RetailCartItem.retailCartItemBuilder()
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .state(State.valueOf(productStatus))
                        .status(INACTIVE)
                        .build())
                .build();
        List<RetailCartItem> rawRetailCartItems = Arrays.asList(activeAndListed, activeAndNotListed, inActiveAndNotListed);
        user.setRetailCartItems(rawRetailCartItems);

        List<RetailCartItem> actual = retailCartItemService.getAll(user);
        List<RetailCartItem> expected = Collections.singletonList(activeAndListed);

        assertEquals(expected.size(), actual.size());
        assertIterableEquals(expected, actual);
    }

    @Test
    void delete() {
        User user = User.builder()
                .retailCartItems(new ArrayList<>())
                .build();
        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(RetailProduct.retailProductBuilder()
                        .id(1)
                        .build())
                .build();
        user.getRetailCartItems().add(retailCartItem);

        doAnswer(i -> user.getRetailCartItems().remove(retailCartItem))
                .when(retailCartItemRepository)
                .delete(retailCartItem);

        retailCartItemService.delete(retailCartItem);
        List<RetailCartItem> actual = user.getRetailCartItems();
        List<RetailCartItem> expected = Collections.emptyList();

        verify(retailCartItemRepository).delete(retailCartItem);
        assertIterableEquals(expected, actual);
    }

    @Test
    void cannotAddToCartProductThatAlreadyExistsInCartItems() {
        // Mock Data
        User user = User.builder()
                .retailCartItems(new ArrayList<>())
                .build();

        RetailProduct retailProduct = new RetailProduct();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();
        user.getRetailCartItems().add(retailCartItem);

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);

        // Calling methods
        // Assertions
        assertThrowsExactly(AlreadyExistException.class, () -> retailCartItemService.save(user, retailCartItemDTO));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartExpiredProduct() {
        // Mock Data
        User user = spy(User.class);

        RetailProduct expiredRetailProduct = RetailProduct.retailProductBuilder()
                .expirationDate(LocalDate.now().minusDays(1))
                .build();

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(expiredRetailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(expiredRetailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));

        // Calling real method
        // Assertion
        assertThrowsExactly(ProductExpiredException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrowsExactly(ProductExpiredException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "ACCEPTED"})
    void cannotAddToCartIfCurrentUserHasAlreadyPendingAndAcceptedOrderToProduct(String orderStatus) {
        // Mock Data
        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .status(Order.Status.valueOf(orderStatus))
                .build();
        user.getRetailOrders().add(retailOrder);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();

        // Assertions
        // Calling the method
        assertThrows(ProductOrderException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(ProductOrderException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartOwnedProduct() {
        // Mock data
        User user = spy(User.class);
        user.setRetailProducts(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        user.getRetailProducts().add(retailProduct);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));

        // Assertions
        // Calling the method
        assertThrows(ResourceOwnedException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(ResourceOwnedException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartAnDeletedProduct() {
        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setStatus(INACTIVE);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));

        // Assertions
        // Calling the method
        assertThrows(ResourceNotFoundException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(ResourceNotFoundException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartAlreadySoldProduct() {
        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setState(SOLD);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isDeleted();
        // Assertions
        // Calling the method
        assertThrows(ProductAlreadySoldException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(ProductAlreadySoldException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    void cannotAddToCartNotListedProduct(String productStatus) {
        // Mock data
        User user = spy(User.class);
        user.setRetailProducts(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setState(State.valueOf(productStatus));

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();

        // Assertions
        // Calling the method
        assertThrows(ProductNotListedException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(ProductNotListedException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartProductIfOrderQuantityExceedsToAvailableQuantityOfProduct() {
        // Mock data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setAvailableQuantity(100);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .orderQuantity(1000)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .orderQuantity(1000)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();

        // Assertions
        // Calling the method
        assertThrowsExactly(OrderQuantiantyExceedsException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrowsExactly(OrderQuantiantyExceedsException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(retailProductService);
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void cannotAddToCartAProductThatRecentlyRejectedWithin24Hours() {
        // Mock data
        User user = spy(User.class);
        user.setRetailOrders(new ArrayList<>());

        RetailProduct retailProduct = spy(RetailProduct.class);
        retailProduct.setRetailOrders(new ArrayList<>());

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .status(REJECTED)
                .updatedAt(LocalDateTime.now())
                .build();
        retailProduct.getRetailOrders().add(retailOrder);
        user.getRetailOrders().add(retailOrder);

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .retailProduct(retailProduct)
                .build();

        RetailCartItemDTO retailCartItemDTO = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .build();

        // Stubbing methods
        when(retailProductService.getById(anyInt())).thenReturn(retailProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isExpired();
        doReturn(false).when(user).hasOrder(any(RetailProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(false).when(retailProduct).isSold();
        doReturn(true).when(retailProduct).isListed();
        doReturn(false).when(retailProduct).isExceedingToAvailableQuantity(anyInt());
        doReturn(true).when(retailProductService).isRejectedBySeller(any(User.class), any(RetailProduct.class));

        // Assertions
        // Calling the method
        assertThrows(BuyerAlreadyRejectedException.class, () -> retailCartItemService.save(user, retailCartItemDTO));
        assertThrows(BuyerAlreadyRejectedException.class, () -> retailCartItemService.orderCartItem(user, retailCartItem));

        // Behavior verification
        verifyNoMoreInteractions(addressService, retailCartItemMapper, retailCartItemRepository, retailOrderRepository);
    }

    @Test
    void save() {
        User user = new User();
        user.setRetailProducts(new ArrayList<>());
        user.setRetailCartItems(new ArrayList<>());
        user.setRetailOrders(new ArrayList<>());
        user.setDeliveryAddresses(new ArrayList<>());

        RetailProduct retailProduct = getMockRetailProduct();

        RetailCartItemDTO dto = RetailCartItemDTO.retailCartItemDTOBuilder()
                .productId(1)
                .orderQuantity(20)
                .deliveryAddressId(1)
                .build();

        RetailCartItem retailCartItem = new RetailCartItem();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();
        user.getDeliveryAddresses().add(deliveryAddress);

        double price = 50_000.00;
        when(retailProductService.getById(dto.getProductId())).thenReturn(retailProduct);
        when(retailProductService.calculateOrderPrice(retailProduct, dto.getOrderQuantity())).thenReturn(price);
        when(addressService.getDeliveryAddressById(user, dto.getDeliveryAddressId())).thenReturn(deliveryAddress);
        when(retailCartItemMapper.toEntity(dto, user, deliveryAddress, price, retailProduct)).thenReturn(retailCartItem);
        when(retailCartItemRepository.save(retailCartItem)).thenReturn(retailCartItem);

        retailCartItemService.save(user, dto);

        verify(retailProductService).isRejectedBySeller(user, retailProduct);
        verify(retailProductService).calculateOrderPrice(retailProduct, dto.getOrderQuantity());
        verify(addressService).getDeliveryAddressById(user, dto.getDeliveryAddressId());
        verify(retailCartItemMapper).toEntity(dto, user, deliveryAddress, price, retailProduct);
        verify(retailCartItemRepository).save(retailCartItem);
        assertDoesNotThrow(() -> retailCartItemService.save(user, dto));
    }

    @Test
    void orderCartItem() {
        User user = new User();
        user.setRetailProducts(new ArrayList<>());
        user.setRetailCartItems(new ArrayList<>());
        user.setRetailOrders(new ArrayList<>());

        RetailProduct retailProduct = getMockRetailProduct();

        RetailCartItem retailCartItem = RetailCartItem.retailCartItemBuilder()
                .id(1)
                .retailProduct(retailProduct)
                .build();
        user.getRetailCartItems().add(retailCartItem);

        RetailOrder retailOrder = new RetailOrder();

        when(retailCartItemMapper.cartItemToOrder(retailCartItem)).thenReturn(retailOrder);
        doAnswer(i -> user.getRetailCartItems().remove(retailCartItem))
                .when(retailCartItemRepository)
                .delete(retailCartItem);
        when(retailOrderRepository.save(retailOrder)).thenReturn(retailOrder);

        retailCartItemService.orderCartItem(user, retailCartItem);

        assertFalse(user.getRetailCartItems().contains(retailCartItem));
        verify(retailCartItemMapper).cartItemToOrder(retailCartItem);
        verify(retailCartItemRepository).delete(retailCartItem);
        verify(retailOrderRepository).save(retailOrder);
        assertDoesNotThrow(() -> retailCartItemService.orderCartItem(user, retailCartItem));
    }

    @Test
    void getByProduct() {
        User user = new User();

        RetailProduct retailProduct1 = RetailProduct.retailProductBuilder()
                .id(1)
                .build();

        RetailProduct retailProduct2 = RetailProduct.retailProductBuilder()
                .id(2)
                .build();

        RetailCartItem expected = RetailCartItem.retailCartItemBuilder()
                .id(1)
                .retailProduct(retailProduct1)
                .build();
        List<RetailCartItem> retailCartItems = Arrays.asList(
                expected,
                RetailCartItem.retailCartItemBuilder()
                        .id(2)
                        .retailProduct(retailProduct2)
                        .build()
        );
        user.setRetailCartItems(retailCartItems);

        RetailCartItem actual = retailCartItemService.getByProduct(user, retailProduct1);

        assertEquals(expected, actual);
        assertDoesNotThrow(() -> retailCartItemService.getByProduct(user, retailProduct1));
    }

    private RetailProduct getMockRetailProduct() {
        return RetailProduct.retailProductBuilder()
                .id(1)
                .expirationDate(LocalDate.now().plusDays(20))
                .status(ACTIVE)
                .state(State.LISTING)
                .availableQuantity(100)
                .pricePerUnit(20)
                .quantityPerUnit(5)
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .retailUnit(RetailUnit.retailUnitBuilder()
                        .name("Retail unit")
                        .build())
                .picture("Picture")
                .build();
    }
}