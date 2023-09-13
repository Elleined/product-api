package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    @Override
    public Product getById(int id) throws ResourceNotFoundException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id of " + id + " does not exists!"));
        if (product.isDeleted()) throw new ResourceNotFoundException("Product with id of " + id + " does not exists or might already been deleted!");
        return product;
    }

    @Override
    public List<Product> getAllExcept(User currentUser) {
        List<Product> userProducts = currentUser.getProducts();

        List<Product> premiumUserProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.LISTING))
                .toList();

        List<Product> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.LISTING))
                .toList();

        List<Product> products = new ArrayList<>();
        products.addAll(premiumUserProducts);
        products.addAll(regularUserProducts);
        products.removeAll(userProducts);
        return products;
    }

    @Override
    public double calculateOrderPrice(Product product, int userOrderQuantity) {
        return product.getPricePerUnit() * userOrderQuantity;
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
        return totalPrice;
    }

    @Override
    public Set<Product> getAllById(Set<Integer> productsToBeListedId) {
        return productRepository.findAllById(productsToBeListedId).stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void deleteExpiredProducts() {
        List<Product> expiredProducts = productRepository.findAll().stream()
                .filter(Product::isExpired)
                .toList();

        // Pending products
        expiredProducts.stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.PENDING)
                .forEach(product -> {
                    product.setStatus(Product.Status.INACTIVE);
                    product.setState(Product.State.EXPIRED);
                    updatePendingAndAcceptedOrderStatus(product.getOrders());
                });

        // Listing products
        expiredProducts.stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.LISTING)
                .forEach(product -> {
                    product.setStatus(Product.Status.INACTIVE);
                    product.setState(Product.State.EXPIRED);
                    updatePendingAndAcceptedOrderStatus(product.getOrders());
                });
        productRepository.saveAll(expiredProducts);
    }

    private void updatePendingAndAcceptedOrderStatus(List<OrderItem> orderItems) {
        List<OrderItem> pendingOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .toList();

        List<OrderItem> acceptedOrders = orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .toList();

        pendingOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        acceptedOrders.forEach(orderItem -> {
            orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });

        log.debug("Pending order items with ids {} are set to {}", pendingOrders.stream().map(OrderItem::getId).toList(), OrderItem.OrderItemStatus.CANCELLED);
        log.debug("Accepted order items with ids {} are set to {}", acceptedOrders.stream().map(OrderItem::getId).toList(), OrderItem.OrderItemStatus.CANCELLED);
    }
}
