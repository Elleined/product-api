package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public boolean isDeleted(Product product) {
        return product.getStatus() == Product.Status.INACTIVE;
    }


    @Override
    public Product getById(int id) throws ResourceNotFoundException {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id of " + id + " does not exists!"));
    }

    @Override
    public List<Product> getAllExcept(User currentUser) {
        List<Product> userProducts = currentUser.getProducts();

        List<Product> products = new ArrayList<>(productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.LISTING)
                .toList());
        products.removeAll(userProducts);

        return products;
    }

    @Override
    public boolean isProductHasPendingOrder(Product product) {
        return product.getOrders().stream().anyMatch(order -> order.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING);
    }

    @Override
    public boolean isProductHasAcceptedOrder(Product product) {
        return product.getOrders().stream().anyMatch(order -> order.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED);
    }

    @Override
    public boolean isSellerAlreadyRejectedBuyerForThisProduct(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getProduct().equals(product))
                .anyMatch(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.REJECTED);
    }

    @Override
    public boolean isExceedingToAvailableQuantity(Product product, int userOrderQuantity) {
        return userOrderQuantity > product.getAvailableQuantity();
    }

    @Override
    public boolean isNotExactToQuantityPerUnit(Product product, int userOrderQuantity) {
        return userOrderQuantity % product.getQuantityPerUnit() != 0;
    }

    @Override
    public double calculatePrice(Product product, int userOrderQuantity) {
        int tempOrderQuantity = userOrderQuantity;
        int counter = 0;
        while (tempOrderQuantity > 0) {
            if (tempOrderQuantity % product.getQuantityPerUnit() == 0) counter++;
            tempOrderQuantity -= product.getQuantityPerUnit();
        }

        double totalPrice = product.getPricePerUnit() * counter;
        log.trace("Total price {}", totalPrice);
        return totalPrice;
    }

    @Override
    public boolean isCriticalFieldsChanged(Product product, ProductDTO productDTO) {
        return product.getPricePerUnit() != productDTO.getPricePerUnit() ||
                product.getAvailableQuantity() != productDTO.getAvailableQuantity() ||
                product.getQuantityPerUnit() != productDTO.getQuantityPerUnit();
    }

}
