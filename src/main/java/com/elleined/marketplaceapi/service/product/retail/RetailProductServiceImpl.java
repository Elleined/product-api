package com.elleined.marketplaceapi.service.product.retail;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.RetailOrderRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RetailProductServiceImpl implements RetailProductService {
    private final RetailProductRepository retailProductRepository;
    private final RetailOrderRepository retailOrderRepository;

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    @Override
    public RetailProduct getById(int productId) throws ResourceNotFoundException {
        return retailProductRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Retail product with id of " + productId + " doesn't exists!"));
    }

    @Override
    public List<RetailProduct> getAllExcept(User currentUser) {
        List<RetailProduct> userProducts = currentUser.getRetailProducts();

        List<RetailProduct> premiumUserProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getRetailProducts)
                .flatMap(Collection::stream)
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
                .toList();

        List<RetailProduct> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getRetailProducts)
                .flatMap(Collection::stream)
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
                .toList();

        List<RetailProduct> products = new ArrayList<>();
        products.addAll(premiumUserProducts);
        products.addAll(regularUserProducts);
        products.removeAll(userProducts);
        return products;
    }

    @Override
    public Set<RetailProduct> getAllById(Set<Integer> productsToBeListedId) {
        return new HashSet<>(retailProductRepository.findAllById(productsToBeListedId));
    }

    @Override
    public List<RetailProduct> getAllByState(User seller, Product.State state) {
        return seller.getRetailProducts().stream()
                .filter(Product::isNotDeleted)
                .filter(product -> product.getState() == state)
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();
    }

    @Override
    public void deleteExpiredProducts() {
        List<RetailProduct> expiredProducts = retailProductRepository.findAll().stream()
                .filter(RetailProduct::isExpired)
                .toList();

        // Pending products
        expiredProducts.stream()
                .filter(Product::isNotDeleted)
                .filter(Product::isPending)
                .forEach(retailProduct -> {
                    retailProduct.setState(Product.State.EXPIRED);
                    updateAllPendingAndAcceptedOrders(retailProduct);
                });

        // Listing retailProducts
        expiredProducts.stream()
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
                .forEach(retailProduct -> {
                    retailProduct.setState(Product.State.EXPIRED);
                    updateAllPendingAndAcceptedOrders(retailProduct);
                });
        retailProductRepository.saveAll(expiredProducts);
    }

    @Override
    public List<RetailProduct> searchProductByCropName(String cropName) {
        return retailProductRepository.searchProductByCropName(cropName).stream()
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
                .toList();
    }

    @Override
    public List<RetailProduct> getByDateRange(User seller, LocalDateTime start, LocalDateTime end) {
        return seller.getRetailProducts().stream()
                .filter(product -> product.getListingDate().equals(start)
                        || (product.getListingDate().isAfter(start) && product.getListingDate().isBefore(end))
                        || product.getListingDate().equals(end))
                .toList();
    }

    @Override
    public void updateAllPendingAndAcceptedOrders(RetailProduct retailProduct, Order.Status status) {
        List<RetailOrder> pendingOrders = retailProduct.getRetailOrders().stream()
                .filter(Order::isPending)
                .toList();

        List<RetailOrder> acceptedOrders = retailProduct.getRetailOrders().stream()
                .filter(Order::isAccepted)
                .toList();

        pendingOrders.forEach(orderItem -> {
            orderItem.setStatus(status);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        acceptedOrders.forEach(orderItem -> {
            orderItem.setStatus(status);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        retailOrderRepository.saveAll(pendingOrders);
        retailOrderRepository.saveAll(acceptedOrders);
    }

    @Override
    public double calculateOrderPrice(RetailProduct retailProduct, int userOrderQuantity) {
        return retailProduct.getPricePerUnit() * userOrderQuantity;
    }

    @Override
    public double calculateTotalPrice(RetailProduct retailProduct) {
        int availableQuantity = retailProduct.getAvailableQuantity();
        int quantityPerUnit = retailProduct.getQuantityPerUnit();
        double pricePerUnit = retailProduct.getPricePerUnit();

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
}
