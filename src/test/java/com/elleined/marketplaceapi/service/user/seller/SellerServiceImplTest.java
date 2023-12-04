package com.elleined.marketplaceapi.service.user.seller;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.dto.product.WholeSaleProductDTO;
import com.elleined.marketplaceapi.dto.product.sale.request.SaleRetailProductRequest;
import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.mapper.product.RetailProductMapper;
import com.elleined.marketplaceapi.mapper.product.WholeSaleProductMapper;
import com.elleined.marketplaceapi.mapper.product.sale.SaleRetailProductMapper;
import com.elleined.marketplaceapi.mapper.product.sale.SaleWholeSaleProductMapper;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleRetailProduct;
import com.elleined.marketplaceapi.model.product.sale.SaleWholeSaleProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.unit.WholeSaleUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import com.elleined.marketplaceapi.repository.product.sale.SaleRetailProductRepository;
import com.elleined.marketplaceapi.repository.product.sale.SaleWholeSaleProductRepository;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplTest {

    @Mock
    private WholeSaleOrderRepository wholeSaleOrderRepository;
    @Mock
    private RetailOrderRepository retailOrderRepository;
    @Mock
    private PrivateChatRoomService privateChatRoomService;
    @Mock
    private RetailProductRepository retailProductRepository;
    @Mock
    private WholeSaleProductRepository wholeSaleProductRepository;
    @Mock
    private RetailProductService retailProductService;
    @Mock
    private WholeSaleProductService wholeSaleProductService;
    @Mock
    private WholeSaleProductMapper wholeSaleProductMapper;
    @Mock
    private RetailProductMapper retailProductMapper;
    @Mock
    private ImageUploader imageUploader;
    @Mock
    private CropService cropService;
    @Mock
    private SaleRetailProductRepository saleRetailProductRepository;
    @Mock
    private SaleRetailProductMapper saleRetailProductMapper;
    @Mock
    private SaleWholeSaleProductRepository saleWholeSaleProductRepository;
    @Mock
    private SaleWholeSaleProductMapper saleWholeSaleProductMapper;
    @Mock
    private RetailUnitService retailUnitService;
    @Mock
    private WholeSaleUnitService wholeSaleUnitService;

    @InjectMocks
    private SellerServiceImpl sellerService;

    @Test
    void saleProduct() {
        // Expected and Actual Value
        double expectedTotalPrice = 900;
        double expectedSalePrice = 899;

        // Mock Data
        User seller = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);
        SaleRetailProductRequest saleWholeSaleRequest = SaleRetailProductRequest.saleRetailProductRequestBuilder()
                .salePercentage(90)
                .build();

        // Stubbing methods
        doReturn(true).when(seller).hasProduct(retailProduct);
        doReturn(true).when(retailProduct).isListed();

        when(retailProductService.calculateTotalPrice(any(RetailProduct.class), any(SaleRetailProductRequest.class))).thenReturn(expectedTotalPrice);
        when(retailProductService.calculateSalePrice(anyDouble(), anyInt())).thenReturn(expectedSalePrice);
        when(saleRetailProductMapper.toEntity(any(SaleRetailProductRequest.class), any(RetailProduct.class))).thenReturn(new SaleRetailProduct());
        when(saleRetailProductRepository.save(any(SaleRetailProduct.class))).thenReturn(new SaleRetailProduct());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saleProduct(seller, retailProduct, saleWholeSaleRequest, ));

        // Behavior Verifications
        verify(retailProductService).calculateTotalPrice(any(RetailProduct.class), any(SaleRetailProductRequest.class));
        verify(retailProductService).calculateSalePrice(anyDouble(), anyInt());
        verify(saleRetailProductMapper).toEntity(any(SaleRetailProductRequest.class), any(RetailProduct.class));
        verify(saleRetailProductRepository).save(any(SaleRetailProduct.class));
    }

    @Test
    void wholeSaleSaleProduct() {
        // Expected and Actual Value
        double expectedTotalPrice = 900;
        int expectedSalePrice = 899;

        // Mock Data
        User seller = spy(User.class);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);
        wholeSaleProduct.setPrice(new BigDecimal(expectedTotalPrice));

        SaleWholeSaleRequest saleWholeSaleRequest = SaleWholeSaleRequest.saleWholeSaleProductRequestBuilder()
                .salePercentage(90)
                .build();

        // Stubbing methods
        doReturn(true).when(seller).hasProduct(wholeSaleProduct);
        doReturn(true).when(wholeSaleProduct).isListed();

        when(wholeSaleProductService.calculateSalePrice(anyDouble(), anyInt())).thenReturn((double) expectedSalePrice);
        when(saleWholeSaleProductMapper.toEntity(any(WholeSaleProduct.class), anyInt(), anyDouble())).thenReturn(new SaleWholeSaleProduct());
        when(wholeSaleProductRepository.save(any(WholeSaleProduct.class))).thenReturn(new WholeSaleProduct());
        when(saleWholeSaleProductRepository.save(any(SaleWholeSaleProduct.class))).thenReturn(new SaleWholeSaleProduct());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saleProduct(seller, wholeSaleProduct, saleWholeSaleRequest));

        // Behavior Verifications
        verify(wholeSaleProductService).calculateSalePrice(anyDouble(), anyInt());
        verify(saleWholeSaleProductMapper).toEntity(any(WholeSaleProduct.class), anyInt(), anyDouble());
        verify(wholeSaleProductRepository).save(any(WholeSaleProduct.class));
        verify(saleWholeSaleProductRepository).save(any(SaleWholeSaleProduct.class));
    }

    @Test
    @DisplayName("save retail product scenario 1: crop name should be saved if doesn't exist in database")
    void saveProduct() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()
                .cropName("Crop")
                .build();

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();

        doReturn(true).when(cropService).notExist(anyString());
        when(cropService.save(anyString())).thenReturn(new Crop());
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(retailUnitService.getById(anyInt())).thenReturn(new RetailUnit());
        when(retailProductMapper.toEntity(any(RetailProductDTO.class), any(User.class), any(Crop.class), any(RetailUnit.class), anyString())).thenReturn(new RetailProduct());
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saveProduct(user, retailProductDTO, MultiPartFileDataFactory.notEmpty()));

        // Behavior Verifications
        verify(cropService).notExist(anyString());
        verify(cropService).save(anyString());
        verify(cropService).getByName(anyString());
        verify(retailUnitService).getById(anyInt());
        verify(retailProductMapper).toEntity(any(RetailProductDTO.class), any(User.class), any(Crop.class), any(RetailUnit.class), anyString());
        verify(retailProductRepository).save(any(RetailProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    @DisplayName("save retail product scenario 2: crop name should not be saved exists in database")
    void saveRetailProductScenario2CropNameExistsInDatabase() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()
                .cropName("Crop")
                .build();

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();

        doReturn(false).when(cropService).notExist(anyString());
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(retailUnitService.getById(anyInt())).thenReturn(new RetailUnit());
        when(retailProductMapper.toEntity(any(RetailProductDTO.class), any(User.class), any(Crop.class), any(RetailUnit.class), anyString())).thenReturn(new RetailProduct());
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saveProduct(user, retailProductDTO, MultiPartFileDataFactory.notEmpty()));

        // Behavior Verifications
        verify(cropService).notExist(anyString());
        verify(cropService).getByName(anyString());
        verify(retailUnitService).getById(anyInt());
        verify(retailProductMapper).toEntity(any(RetailProductDTO.class), any(User.class), any(Crop.class), any(RetailUnit.class), anyString());
        verify(retailProductRepository).save(any(RetailProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    @DisplayName("save whole sale product scenario 1: crop name should be saved if doesn't exist in database")
    void wholeSaleSaveProduct() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        WholeSaleProductDTO wholeSaleProductDTO = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .cropName("Crop")
                .totalPrice(1)
                .build();

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();

        doReturn(true).when(cropService).notExist(anyString());
        when(cropService.save(anyString())).thenReturn(new Crop());
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(wholeSaleUnitService.getById(anyInt())).thenReturn(new WholeSaleUnit());
        when(wholeSaleProductMapper.toEntity(any(WholeSaleProductDTO.class), any(User.class), any(Crop.class), any(WholeSaleUnit.class), anyString())).thenReturn(new WholeSaleProduct());
        when(wholeSaleProductRepository.save(any(WholeSaleProduct.class))).thenReturn(new WholeSaleProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saveProduct(user, wholeSaleProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertNotEquals(0, wholeSaleProductDTO.getTotalPrice());

        // Behavior Verifications
        verify(cropService).notExist(anyString());
        verify(cropService).save(anyString());
        verify(cropService).getByName(anyString());
        verify(wholeSaleUnitService).getById(anyInt());
        verify(wholeSaleProductMapper).toEntity(any(WholeSaleProductDTO.class), any(User.class), any(Crop.class), any(WholeSaleUnit.class), anyString());
        verify(wholeSaleProductRepository).save(any(WholeSaleProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    @DisplayName("save whole sale product scenario 2: crop name should not be saved if exist in database")
    void wholeSaleSaveProductScenario2CropNameExistsInDatabase() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        WholeSaleProductDTO wholeSaleProductDTO = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .cropName("Crop")
                .totalPrice(1)
                .build();

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();

        doReturn(false).when(cropService).notExist(anyString());
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(wholeSaleUnitService.getById(anyInt())).thenReturn(new WholeSaleUnit());
        when(wholeSaleProductMapper.toEntity(any(WholeSaleProductDTO.class), any(User.class), any(Crop.class), any(WholeSaleUnit.class), anyString())).thenReturn(new WholeSaleProduct());
        when(wholeSaleProductRepository.save(any(WholeSaleProduct.class))).thenReturn(new WholeSaleProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.saveProduct(user, wholeSaleProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertNotEquals(0, wholeSaleProductDTO.getTotalPrice());

        // Behavior Verifications
        verify(cropService).notExist(anyString());
        verify(cropService).getByName(anyString());
        verify(wholeSaleUnitService).getById(anyInt());
        verify(wholeSaleProductMapper).toEntity(any(WholeSaleProductDTO.class), any(User.class), any(Crop.class), any(WholeSaleUnit.class), anyString());
        verify(wholeSaleProductRepository).save(any(WholeSaleProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    void updateProduct() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        RetailProduct retailProduct = spy(RetailProduct.class);

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()
                .cropName("Crop")
                .build();

        // Stubbing methods
        doReturn(false).when(retailProduct).hasAcceptedOrder();
        doReturn(false).when(retailProduct).hasPendingOrder();
        doReturn(false).when(retailProduct).hasSoldOrder();
        doReturn(false).when(user).isNotVerified();
        doReturn(true).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isSold();
        doReturn(false).when(retailProduct).isDeleted();
        doReturn(true).when(cropService).notExist(anyString());

        doNothing().when(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(cropService.save(anyString())).thenReturn(new Crop());
        when(retailUnitService.getById(anyInt())).thenReturn(new RetailUnit());
        when(retailProductMapper.toUpdate(any(RetailProduct.class), any(RetailProductDTO.class), any(RetailUnit.class), any(Crop.class), anyString())).thenReturn(new RetailProduct());
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.updateProduct(user, retailProduct, retailProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(Product.State.PENDING, retailProduct.getState());
        // Behavior Verifications
        verify(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
        verify(cropService).notExist(anyString());
        verify(cropService).save(anyString());
        verify(cropService).getByName(anyString());
        verify(retailUnitService).getById(anyInt());
        verify(retailProductMapper).toUpdate(any(RetailProduct.class), any(RetailProductDTO.class), any(RetailUnit.class), any(Crop.class), anyString());
        verify(retailProductRepository).save(any(RetailProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    void wholeSaleUpdateProduct() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        WholeSaleProductDTO wholeSaleProductDTO = WholeSaleProductDTO.wholeSaleProductDTOBuilder()
                .cropName("Crop")
                .build();

        // Stubbing methods
        doReturn(false).when(wholeSaleProduct).hasAcceptedOrder();
        doReturn(false).when(wholeSaleProduct).hasPendingOrder();
        doReturn(false).when(wholeSaleProduct).hasSoldOrder();
        doReturn(false).when(user).isNotVerified();
        doReturn(true).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(false).when(wholeSaleProduct).isDeleted();
        doReturn(true).when(cropService).notExist(anyString());

        doNothing().when(wholeSaleProductService).updateAllPendingAndAcceptedOrders(any(WholeSaleProduct.class), any(Order.Status.class));
        when(cropService.getByName(anyString())).thenReturn(new Crop());
        when(cropService.save(anyString())).thenReturn(new Crop());
        when(wholeSaleUnitService.getById(anyInt())).thenReturn(new WholeSaleUnit());
        when(wholeSaleProductMapper.toUpdate(any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(Crop.class), any(WholeSaleUnit.class), anyString())).thenReturn(new WholeSaleProduct());
        when(wholeSaleProductRepository.save(any(WholeSaleProduct.class))).thenReturn(new WholeSaleProduct());
        doNothing().when(imageUploader).upload(anyString(), any(MultipartFile.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.updateProduct(user, wholeSaleProduct, wholeSaleProductDTO, MultiPartFileDataFactory.notEmpty()));
        assertEquals(Product.State.PENDING, wholeSaleProduct.getState());

        // Behavior Verifications
        verify(wholeSaleProductService).updateAllPendingAndAcceptedOrders(any(WholeSaleProduct.class), any(Order.Status.class));
        verify(cropService).notExist(anyString());
        verify(cropService).save(anyString());
        verify(cropService).getByName(anyString());
        verify(wholeSaleUnitService).getById(anyInt());
        verify(wholeSaleProductMapper).toUpdate(any(WholeSaleProduct.class), any(WholeSaleProductDTO.class), any(Crop.class), any(WholeSaleUnit.class), anyString());
        verify(wholeSaleProductRepository).save(any(WholeSaleProduct.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
    }

    @Test
    void deleteProduct() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        RetailProduct retailProduct = spy(RetailProduct.class);

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();
        doReturn(true).when(user).hasProduct(any(RetailProduct.class));
        doReturn(false).when(retailProduct).isSold();
        doReturn(false).when(retailProduct).hasPendingOrder();
        doReturn(false).when(retailProduct).hasAcceptedOrder();

        doNothing().when(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.deleteProduct(user, retailProduct));
        assertEquals(Product.Status.INACTIVE, retailProduct.getStatus());

        // Behavior Verifications
        verify(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
        verify(retailProductRepository).save(any(RetailProduct.class));
    }

    @Test
    void wholeSaleDeleteProduct() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        // Stubbing methods
        doReturn(false).when(user).isNotVerified();
        doReturn(true).when(user).hasProduct(any(WholeSaleProduct.class));
        doReturn(false).when(wholeSaleProduct).isSold();
        doReturn(false).when(wholeSaleProduct).hasPendingOrder();
        doReturn(false).when(wholeSaleProduct).hasAcceptedOrder();

        doNothing().when(wholeSaleProductService).updateAllPendingAndAcceptedOrders(any(WholeSaleProduct.class), any(Order.Status.class));
        when(wholeSaleProductRepository.save(any(WholeSaleProduct.class))).thenReturn(new WholeSaleProduct());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.deleteProduct(user, wholeSaleProduct));
        assertEquals(Product.Status.INACTIVE, wholeSaleProduct.getStatus());

        // Behavior Verifications
        verify(wholeSaleProductService).updateAllPendingAndAcceptedOrders(any(WholeSaleProduct.class), any(Order.Status.class));
        verify(wholeSaleProductRepository).save(any(WholeSaleProduct.class));
    }

    @Test
    void acceptOrder() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        RetailProduct retailProduct = spy(RetailProduct.class);

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .build();

        // Stubbing methods
        doReturn(false).when(retailProduct).hasNoAvailableQuantity(anyInt());
        doReturn(false).when(retailProduct).isRejected();
        doReturn(true).when(user).hasSellableProductOrder(any(RetailOrder.class));

        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(new RetailOrder());
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.acceptOrder(user, retailOrder, "Message"));
        assertEquals(Order.Status.ACCEPTED, retailOrder.getStatus());
        assertNotNull(retailOrder.getUpdatedAt());
        assertNotNull(retailOrder.getSellerMessage());

        // Behavior Verifications
        verify(retailOrderRepository).save(any(RetailOrder.class));
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        WholeSaleOrder retailOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .build();

        // Stubbing methods
        doReturn(false).when(wholeSaleProduct).isRejected();
        doReturn(true).when(user).hasSellableProductOrder(any(WholeSaleOrder.class));

        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(new WholeSaleOrder());
        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.acceptOrder(user, retailOrder, "Message"));
        assertEquals(Order.Status.ACCEPTED, retailOrder.getStatus());
        assertNotNull(retailOrder.getUpdatedAt());
        assertNotNull(retailOrder.getSellerMessage());

        // Behavior Verifications
        verify(wholeSaleOrderRepository).save(any(WholeSaleOrder.class));
    }

    @Test
    void rejectOrder() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        RetailOrder retailOrder = spy(RetailOrder.class);

        // Stubbing methods
        doReturn(true).when(user).hasSellableProductOrder(any(RetailOrder.class));
        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(new RetailOrder());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.rejectOrder(user, retailOrder, "Message"));
        assertEquals(Order.Status.REJECTED, retailOrder.getStatus());
        assertNotNull(retailOrder.getUpdatedAt());
        assertNotNull(retailOrder.getSellerMessage());

        // Behavior Verifications
        verify(retailOrderRepository).save(any(RetailOrder.class));
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        WholeSaleOrder wholeSaleOrder = spy(WholeSaleOrder.class);

        // Stubbing methods
        doReturn(true).when(user).hasSellableProductOrder(any(WholeSaleOrder.class));
        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(new WholeSaleOrder());

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.rejectOrder(user, wholeSaleOrder, "Message"));
        assertEquals(Order.Status.REJECTED, wholeSaleOrder.getStatus());
        assertNotNull(wholeSaleOrder.getUpdatedAt());
        assertNotNull(wholeSaleOrder.getSellerMessage());

        // Behavior Verifications
        verify(wholeSaleOrderRepository).save(any(WholeSaleOrder.class));
    }

    @Test
    @DisplayName("sold retail order scenario 1: available quantity are not yet below zero hence only marking the order sold not the product")
    void retailSoldOrderCase1() {
        // Expected and Actual Value

        // Mock Data

        User user = spy(User.class);
        RetailProduct retailProduct = spy(RetailProduct.class);

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .purchaser(new User())
                .build();

        // Stubbing methods
        doReturn(true).when(user).hasSellableProductOrder(any(RetailOrder.class));
        doReturn(false).when(retailProduct).hasNoAvailableQuantity(anyInt());
        when(privateChatRoomService.getChatRoom(any(User.class), any(User.class), any(Product.class))).thenReturn(new PrivateChatRoom());
        doNothing().when(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());
        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(new RetailOrder());
        doNothing().when(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.soldOrder(user, retailOrder));
        assertEquals(Order.Status.SOLD, retailOrder.getStatus());

        // Behavior Verifications
        verify(privateChatRoomService).getChatRoom(any(User.class), any(User.class), any(Product.class));
        verify(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
        verify(retailProductRepository).save(any(RetailProduct.class));
        verify(retailOrderRepository).save(any(RetailOrder.class));
        verify(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
    }

    @Test
    @DisplayName("sold retail order scenario 2: available quantity are below zero hence only marking the order sold and the product sold")
    void retailSoldOrderCase2() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        RetailProduct retailProduct = spy(RetailProduct.class);

        RetailOrder retailOrder = RetailOrder.retailOrderBuilder()
                .retailProduct(retailProduct)
                .purchaser(new User())
                .build();

        // Stubbing methods
        doReturn(true).when(user).hasSellableProductOrder(any(RetailOrder.class));
        doReturn(true).when(retailProduct).hasNoAvailableQuantity(anyInt());

        when(privateChatRoomService.getChatRoom(any(User.class), any(User.class), any(Product.class))).thenReturn(new PrivateChatRoom());
        doNothing().when(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
        when(retailProductRepository.save(any(RetailProduct.class))).thenReturn(new RetailProduct());
        when(retailOrderRepository.save(any(RetailOrder.class))).thenReturn(new RetailOrder());
        doNothing().when(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.soldOrder(user, retailOrder));
        assertEquals(Order.Status.SOLD, retailOrder.getStatus());
        assertEquals(Product.State.SOLD, retailProduct.getState());

        // Behavior Verifications
        verify(privateChatRoomService).getChatRoom(any(User.class), any(User.class), any(Product.class));
        verify(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
        verify(retailProductRepository).save(any(RetailProduct.class));
        verify(retailOrderRepository).save(any(RetailOrder.class));
        verify(retailProductService).updateAllPendingAndAcceptedOrders(any(RetailProduct.class), any(Order.Status.class));
    }
    @Test
    void wholeSaleSoldOrder() {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);
        WholeSaleProduct wholeSaleProduct = spy(WholeSaleProduct.class);

        WholeSaleOrder wholeSaleOrder = WholeSaleOrder.wholeSaleOrderBuilder()
                .wholeSaleProduct(wholeSaleProduct)
                .purchaser(new User())
                .build();

        // Stubbing methods
        doReturn(true).when(user).hasSellableProductOrder(any(WholeSaleOrder.class));

        when(privateChatRoomService.getChatRoom(any(User.class), any(User.class), any(Product.class))).thenReturn(new PrivateChatRoom());
        doNothing().when(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
        when(wholeSaleOrderRepository.save(any(WholeSaleOrder.class))).thenReturn(new WholeSaleOrder());
        doNothing().when(wholeSaleProductService).updateAllPendingAndAcceptedOrders(any(WholeSaleProduct.class), any(Order.Status.class));

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> sellerService.soldOrder(user, wholeSaleOrder));
        assertEquals(Order.Status.SOLD, wholeSaleOrder.getStatus());

        // Behavior Verifications
        verify(privateChatRoomService).getChatRoom(any(User.class), any(User.class), any(Product.class));
        verify(privateChatRoomService).deleteAllMessages(any(PrivateChatRoom.class));
    }
}