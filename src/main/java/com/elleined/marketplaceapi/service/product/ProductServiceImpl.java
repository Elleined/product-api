package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
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

import static com.elleined.marketplaceapi.service.user.SellerService.LISTING_FEE_PERCENTAGE;

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
    public double calculateOrderPrice(Product product, int userOrderQuantity) {
        int counter = 0;
        while (userOrderQuantity > 0) {
            if (userOrderQuantity % product.getQuantityPerUnit() == 0) counter++;
            userOrderQuantity -= product.getQuantityPerUnit();
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

    @Override
    public double calculateTotalPrice(ProductDTO productDTO) {
        double pricePerUnit = productDTO.getPricePerUnit();
        int quantityPerUnit = productDTO.getQuantityPerUnit();
        int availableQuantity = productDTO.getAvailableQuantity();

        int counter = 0;
        while (availableQuantity > 0) {
            if (availableQuantity % quantityPerUnit == 0) counter++;
            availableQuantity -= quantityPerUnit;
        }
        log.trace("Counter {}", counter);
        double totalPrice = counter * pricePerUnit;
        log.trace("Total price {}", totalPrice);
        log.trace("Product with total price of {} will have {} of listing fee percentage which is {}", totalPrice, getListingFee(totalPrice), LISTING_FEE_PERCENTAGE);
        return totalPrice;
    }

    @Override
    public double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

}
