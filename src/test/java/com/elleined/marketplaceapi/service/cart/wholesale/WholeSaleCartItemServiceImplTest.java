package com.elleined.marketplaceapi.service.cart.wholesale;

import com.elleined.marketplaceapi.dto.cart.WholeSaleCartItemDTO;
import com.elleined.marketplaceapi.exception.product.ProductAlreadySoldException;
import com.elleined.marketplaceapi.exception.product.ProductNotListedException;
import com.elleined.marketplaceapi.exception.product.order.ProductOrderException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.resource.ResourceOwnedException;
import com.elleined.marketplaceapi.exception.user.buyer.BuyerAlreadyRejectedException;
import com.elleined.marketplaceapi.mapper.cart.WholeSaleCartItemMapper;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.cart.WholeSaleCartItem;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
import com.elleined.marketplaceapi.repository.cart.WholeSaleCartItemRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.SaleStatus.NOT_ON_SALE;
import static com.elleined.marketplaceapi.model.product.Product.State;
import static com.elleined.marketplaceapi.model.product.Product.State.LISTING;
import static com.elleined.marketplaceapi.model.product.Product.State.SOLD;
import static com.elleined.marketplaceapi.model.product.Product.Status;
import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholeSaleCartItemServiceImplTest {

    @Mock
    private WholeSaleProductServiceImpl wholeSaleProductService;
    @Mock
    private WholeSaleCartItemMapper wholeSaleCartItemMapper;
    @Mock
    private WholeSaleCartItemRepository wholeSaleCartItemRepository;
    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Mock
    private AddressService addressService;

    @InjectMocks
    private WholeSaleCartItemServiceImpl wholeSaleCartItemService;

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    void getAll(String productStatus) {
        User user = new User();

        WholeSaleCartItem activeAndListing = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.LISTING)
                        .status(Status.ACTIVE)
                        .build())
                .build();

        WholeSaleCartItem activeAndNotListed = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.valueOf(productStatus))
                        .status(Status.ACTIVE)
                        .build())
                .build();

        WholeSaleCartItem inActiveAndNotListed = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .state(State.valueOf(productStatus))
                        .status(Status.INACTIVE)
                        .build())
                .build();

        List<WholeSaleCartItem> rawWholeSaleCartItems = Arrays.asList(activeAndListing, activeAndNotListed, inActiveAndNotListed);
        user.setWholeSaleCartItems(rawWholeSaleCartItems);

        List<WholeSaleCartItem> actual = wholeSaleCartItemService.getAll(user);
        List<WholeSaleCartItem> expected = Collections.singletonList(activeAndListing);

        assertIterableEquals(expected, actual);
    }

    @Test
    void delete() {
        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(WholeSaleProduct.wholeSaleProductBuilder()
                        .id(1)
                        .build())
                .build();

        wholeSaleCartItemService.delete(wholeSaleCartItem);
        verify(wholeSaleCartItemRepository).delete(wholeSaleCartItem);
    }


    @Test
    void save() {
        User user = User.builder()
                .id(1)
                .wholeSaleProducts(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .deliveryAddresses(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        DeliveryAddress deliveryAddress = DeliveryAddress.deliveryAddressBuilder()
                .id(1)
                .build();
        user.getDeliveryAddresses().add(deliveryAddress);

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .deliveryAddressId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = new WholeSaleCartItem();

        when(wholeSaleProductService.getById(dto.getProductId())).thenReturn(wholeSaleProduct);
        when(addressService.getDeliveryAddressById(user, dto.getDeliveryAddressId())).thenReturn(deliveryAddress);
        when(wholeSaleCartItemMapper.toEntity(dto, user, deliveryAddress, wholeSaleProduct)).thenReturn(wholeSaleCartItem);
        when(wholeSaleCartItemRepository.save(wholeSaleCartItem)).thenReturn(wholeSaleCartItem);

        wholeSaleCartItemService.save(user, dto);

        verify(wholeSaleProductService).isRejectedBySeller(user, wholeSaleProduct);
        verify(addressService).getDeliveryAddressById(user, dto.getDeliveryAddressId());
        verify(wholeSaleCartItemMapper).toEntity(dto, user, deliveryAddress, wholeSaleProduct);
        verify(wholeSaleCartItemRepository).save(wholeSaleCartItem);
        assertDoesNotThrow(() -> wholeSaleCartItemService.save(user, dto));
    }

    @Test
    void orderCartItem() {
        // Mock Data
        User user = User.builder()
                .id(1)
                .wholeSaleProducts(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .deliveryAddresses(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .id(1)
                .wholeSaleProduct(wholeSaleProduct)
                .build();
        user.getWholeSaleCartItems().add(wholeSaleCartItem);

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .id(1)
                .build();

        // Stubbing external dependencies
        when(wholeSaleCartItemMapper.cartItemToOrder(any(WholeSaleCartItem.class))).thenReturn(wholeSaleOrder);
        doAnswer(i -> user.getWholeSaleCartItems().remove(wholeSaleCartItem))
                .when(wholeSaleCartItemRepository)
                .delete(any(WholeSaleCartItem.class));
        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(wholeSaleOrder);

        // Calling the method
        wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem);

        // Behavior Verification
        verify(wholeSaleProductService).isRejectedBySeller(user, wholeSaleProduct);
        verify(wholeSaleCartItemMapper).cartItemToOrder(wholeSaleCartItem);
        verify(wholeSaleCartItemRepository).delete(wholeSaleCartItem);
        verify(wholeSaleOrderRepository).save(wholeSaleOrder);

        // Assertions
        assertFalse(user.getWholeSaleCartItems().contains(wholeSaleCartItem));
        assertDoesNotThrow(() -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));
    }

    @Test
    void getByProduct() {
        // Mock Data
        User user = User.builder()
                .id(1)
                .wholeSaleProducts(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .deliveryAddresses(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();

        WholeSaleCartItem expected = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .id(1)
                .wholeSaleProduct(wholeSaleProduct)
                .build();
        user.getWholeSaleCartItems().add(expected);

        WholeSaleCartItem actual = wholeSaleCartItemService.getByProduct(user, wholeSaleProduct);

        assertEquals(expected, actual);
        assertDoesNotThrow(() -> wholeSaleCartItemService.getByProduct(user, wholeSaleProduct));
    }

    @Test
    @DisplayName("Get all by id")
    void whenGettingAllByIdCartItemShouldBeSortedDescBasedOnCreatedAt() {
        WholeSaleCartItem oldCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .id(1)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        WholeSaleCartItem newCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .id(2)
                .createdAt(LocalDateTime.now().plusDays(1))
                .build();

        List<WholeSaleCartItem> rawCartItems = Arrays.asList(oldCartItem, newCartItem);
        when(wholeSaleCartItemRepository.findAllById(anyIterable())).thenReturn(rawCartItems);

        List<WholeSaleCartItem> actual = wholeSaleCartItemService.getAllById(Arrays.asList(1, 2));

        List<WholeSaleCartItem> expected = Arrays.asList(newCartItem, oldCartItem);
        assertIterableEquals(expected, actual);
    }

    private WholeSaleProduct getMockWholeSaleProduct() {
        return WholeSaleProduct.wholeSaleProductBuilder()
                .id(1)
                .description("Description")
                .availableQuantity(1_000)
                .harvestDate(LocalDate.now())
                .listingDate(LocalDateTime.now())
                .picture("Picture.jpg")
                .state(LISTING)
                .status(ACTIVE)
                .seller(User.builder()
                        .id(1)
                        .userDetails(UserDetails.builder()
                                .firstName("First name")
                                .middleName("Middle name")
                                .lastName("Last name")
                                .build())
                        .shop(Shop.builder()
                                .id(1)
                                .name("Shop name")
                                .build())
                        .build())
                .crop(Crop.builder()
                        .name("Crop")
                        .build())
                .saleStatus(NOT_ON_SALE)
                .wholeSaleUnit(WholeSaleUnit.wholeSaleUnitBuilder()
                        .id(1)
                        .name("Whole sale unit")
                        .build())
                .price(new BigDecimal(5_000))
                .privateChatRooms(new ArrayList<>())
                .wholeSaleOrders(new ArrayList<>())
                .wholeSaleCartItems(new ArrayList<>())
                .build();
    }

    @Test
    void cannotAddToCartProductThatAlreadyExistsInCartItems() {
        User user = spy(User.class);
        user.setWholeSaleCartItems(new ArrayList<>());

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();
        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();
        user.getWholeSaleCartItems().add(wholeSaleCartItem);

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();
        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);

        // Calling the method
        // Assertions
        assertThrowsExactly(AlreadyExistException.class, () -> wholeSaleCartItemService.save(user, dto));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "ACCEPTED"})
    void cannotAddToCartIfCurrentUserHasAlreadyPendingAndAcceptedOrderToProduct(String orderStatus) {
        // Mock Data
        User user = spy(User.class);
        user.setWholeSaleOrders(new ArrayList<>());

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();
        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .status(Order.Status.valueOf(orderStatus))
                .build();
        user.getWholeSaleOrders().add(wholeSaleOrder);

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(wholeSaleProduct);

        // Assertions
        // Calling the method
        assertThrows(ProductOrderException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrows(ProductOrderException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }

    @Test
    void cannotAddToCartOwnedProduct() {
        // Mock data
        User user = spy(User.class);
        user.setWholeSaleProducts(new ArrayList<>());

        WholeSaleProduct wholeSaleProduct = getMockWholeSaleProduct();
        user.getWholeSaleProducts().add(wholeSaleProduct);

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(WholeSaleProduct.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));

        // Calling the method
        assertThrows(ResourceOwnedException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrows(ResourceOwnedException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }

    @Test
    void cannotAddToCartAnDeletedProduct() {
        // Mock data
        User user = spy(User.class);

        WholeSaleProduct wholeSaleProduct = WholeSaleProduct.wholeSaleProductBuilder()
                .status(INACTIVE)
                .build();

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(WholeSaleProduct.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));

        // Calling the method
        assertThrows(ResourceNotFoundException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrows(ResourceNotFoundException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }

    @Test
    void cannotAddToCartAlreadySoldProduct() {
        // Mock data
        User user = spy(User.class);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setState(SOLD);

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(WholeSaleProduct.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(false).when(wholeSaleProduct).isDeleted();

        // Calling the method
        assertThrows(ProductAlreadySoldException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrows(ProductAlreadySoldException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "SOLD", "REJECTED", "EXPIRED"})
    void cannotAddToCartNotListedProduct(String productStatus) {
        // Mock data
        User user = spy(User.class);
        user.setWholeSaleProducts(new ArrayList<>());

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setState(Product.State.valueOf(productStatus));

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(WholeSaleProduct.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isSold();

        // Calling the method
        assertThrowsExactly(ProductNotListedException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrowsExactly(ProductNotListedException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }

    @Test
    void cannotAddToCartAProductThatRecentlyRejectedWithin24Hours() {
        // Mock data
        User user = spy(User.class);
        user.setWholeSaleOrders(new ArrayList<>());

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setWholeSaleOrders(new ArrayList<>());

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .status(Order.Status.REJECTED)
                .updatedAt(LocalDateTime.now())
                .build();
        wholeSaleProduct.getWholeSaleOrders().add(wholeSaleOrder);
        user.getWholeSaleOrders().add(wholeSaleOrder);

        WholeSaleCartItemDTO dto = WholeSaleCartItemDTO.wholeSaleCartItemDTOBuilder()
                .productId(1)
                .build();

        WholeSaleCartItem wholeSaleCartItem = WholeSaleCartItem.wholeSaleCartItemBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        when(wholeSaleProductService.getById(anyInt())).thenReturn(wholeSaleProduct);
        doReturn(false).when(user).isProductAlreadyInCart(any(WholeSaleProduct.class));
        doReturn(false).when(user).hasOrder(any(WholeSaleProduct.class), any(Order.Status.class));
        doReturn(false).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(true).when(wholeSaleProduct).isListed();
        doCallRealMethod().when(wholeSaleProductService).isRejectedBySeller(user, wholeSaleProduct);

        // Calling the method
        assertThrowsExactly(BuyerAlreadyRejectedException.class, () -> wholeSaleCartItemService.save(user, dto));
        assertThrowsExactly(BuyerAlreadyRejectedException.class, () -> wholeSaleCartItemService.orderCartItem(user, wholeSaleCartItem));

        // Behavior verification
        verifyNoInteractions(addressService, wholeSaleCartItemMapper, wholeSaleCartItemRepository, wholeSaleOrderRepository);
    }
}