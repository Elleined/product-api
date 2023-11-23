package com.elleined.marketplaceapi.service.user.seller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.elleined.marketplaceapi.dto.product.RetailProductDTO;
import com.elleined.marketplaceapi.mapper.product.RetailProductMapper;
import com.elleined.marketplaceapi.mapper.product.WholeSaleProductMapper;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.unit.RetailUnit;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import com.elleined.marketplaceapi.service.CropService;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.message.prv.PrivateChatRoomService;
import com.elleined.marketplaceapi.service.product.retail.RetailProductService;
import com.elleined.marketplaceapi.service.product.wholesale.WholeSaleProductService;
import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
    private OrderRepository orderRepository;
    @Mock
    private RetailUnitService retailUnitService;
    @Mock
    private WholeSaleUnitService wholeSaleUnitService;
    @InjectMocks
    private SellerServiceImpl sellerService;

    @Test
    void saleProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleSaleProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void saveProduct() throws IOException {
        // Expected and Actual Value

        // Mock Data
        User user = spy(User.class);

        RetailProductDTO retailProductDTO = RetailProductDTO.retailProductDTOBuilder()

                .build();

        // Stubbing methods
        doReturn(true).when(user).isNotVerified();

        when(cropService.notExist(anyString())).thenReturn(false);
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
    void wholeSaleSaveProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void updateProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleUpdateProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void deleteProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleDeleteProduct() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void acceptOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleAcceptOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void rejectOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleRejectOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void soldOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }

    @Test
    void wholeSaleSoldOrder() {
        // Expected and Actual Value

        // Mock Data

        // Stubbing methods

        // Calling the method

        // Assertions

        // Behavior Verifications
    }
}