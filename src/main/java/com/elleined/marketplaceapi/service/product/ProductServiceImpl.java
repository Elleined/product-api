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
import static com.elleined.marketplaceapi.service.user.SellerService.SUCCESSFUL_TRANSACTION_FEE;

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
    public boolean isSellerAlreadyRejectedBuyer(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getProduct().equals(product))
                .anyMatch(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.REJECTED);
    }

    @Override
    public boolean isExceedingToAvailableQuantity(Product product, int userOrderQuantity) {
        return userOrderQuantity > product.getAvailableQuantity();
    }


    @Override
    public double calculateOrderPrice(Product product, int userOrderQuantity) {
        return product.getPricePerUnit() * userOrderQuantity;
    }

    @Override
    public boolean isCriticalFieldsChanged(Product product, ProductDTO productDTO) {
        return product.getPricePerUnit() != productDTO.getPricePerUnit() ||
                product.getAvailableQuantity() != productDTO.getAvailableQuantity() ||
                product.getQuantityPerUnit() != productDTO.getQuantityPerUnit();
    }

    @Override
    public boolean isProductSold(User currentUser, Product product) {
        return product.getState() == Product.State.SOLD;
    }

    @Override
    public int getAllProductCount() {
        return (int) productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() != Product.State.PENDING)
                .count();
    }

    @Override
    public double calculateTotalPrice(double pricePerUnit, int quantityPerUnit, int availableQuantity) {
        int counter = 0;
        while (availableQuantity > 0) {
            if (availableQuantity <= quantityPerUnit) counter++;
            else if (availableQuantity % quantityPerUnit == 0) counter++;
            availableQuantity -= quantityPerUnit;
        }
        log.trace("Counter {}", counter);
        double totalPrice = counter * pricePerUnit;
        log.trace("Total price {}", totalPrice);
        log.trace("Product with total price of {} will have listing fee of {} which is {} percent of total price", totalPrice, getListingFee(totalPrice), LISTING_FEE_PERCENTAGE);
        return totalPrice;
    }

    @Override
    public double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

    @Override
    public double getSuccessfulTransactionFee(double orderPrice) {
        return (orderPrice * (SUCCESSFUL_TRANSACTION_FEE / 100f));
    }

}
