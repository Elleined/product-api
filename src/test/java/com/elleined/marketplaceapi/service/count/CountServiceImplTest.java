package com.elleined.marketplaceapi.service.count;

import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.OrderRepository;
import com.elleined.marketplaceapi.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.elleined.marketplaceapi.model.product.Product.Status.ACTIVE;
import static com.elleined.marketplaceapi.model.product.Product.Status.INACTIVE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CountServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CountServiceImpl countService;


    @Test
    void getAllUsersCount() {
        // Stubbing methods
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        // Calling the method
        int actual = countService.getAllUsersCount();

        // Assertions
        assertEquals(0, actual);

        // Behavior verification
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersTransactionsCount() {
        // Stubbing methods
        when(orderRepository.findAll()).thenReturn(new ArrayList<>());

        // Calling the method
        int actual = countService.getAllUsersTransactionsCount();

        // Assertions
        assertEquals(0, actual);

        // Behavior verification
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("getAllProductCount")
    void shouldOnlyFetchActiveProduct() {
        // Mock data
        List<Product> rawProducts = Arrays.asList(
                RetailProduct.retailProductBuilder()
                        .status(ACTIVE)
                        .build(),
                RetailProduct.retailProductBuilder()
                        .status(INACTIVE)
                        .build(),
                WholeSaleProduct.wholeSaleProductBuilder()
                        .status(ACTIVE)
                        .build(),
                WholeSaleProduct.wholeSaleProductBuilder()
                        .status(INACTIVE)
                        .build()
        );


        // Stubbing methods
        when(productRepository.findAll()).thenReturn(rawProducts);

        // Calling the method
        int productCount = countService.getAllProductCount();

        // Assertions
        assertEquals(2, productCount);

        // Behavior verification
    }
}