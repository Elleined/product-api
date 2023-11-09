package com.elleined.marketplaceapi.service.product.wholesale;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.RetailOrder;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
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
public class WholeSaleProductServiceImpl implements WholeSaleProductService {
    private final WholeSaleProductRepository wholeSaleProductRepository;

    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;
    @Override
    public WholeSaleProduct getById(int productId) throws ResourceNotFoundException {
        return wholeSaleProductRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Whole product with id of " + productId + " doesn't exists!"));
    }

    @Override
    public List<WholeSaleProduct> getAllExcept(User currentUser) {
        List<WholeSaleProduct> userProducts = currentUser.getWholeSaleProducts();

        List<WholeSaleProduct> premiumUserProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getWholeSaleProducts)
                .flatMap(Collection::stream)
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.LISTING)
                .toList();

        List<WholeSaleProduct> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getWholeSaleProducts)
                .flatMap(Collection::stream)
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.LISTING)
                .toList();

        List<WholeSaleProduct> products = new ArrayList<>();
        products.addAll(premiumUserProducts);
        products.addAll(regularUserProducts);
        products.removeAll(userProducts);
        return products;
    }

    @Override
    public Set<WholeSaleProduct> getAllById(Set<Integer> productsToBeListedId) {
        return new HashSet<>(wholeSaleProductRepository.findAllById(productsToBeListedId));
    }

    @Override
    public List<WholeSaleProduct> getAllByState(User seller, Product.State state) {
        return seller.getWholeSaleProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == state)
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();
    }

    @Override
    public List<WholeSaleProduct> searchProductByCropName(String cropName) {
        return wholeSaleProductRepository.searchProductByCropName(cropName).stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.LISTING)
                .toList();
    }

    @Override
    public List<WholeSaleProduct> getByDateRange(User seller, LocalDateTime start, LocalDateTime end) {
        return seller.getWholeSaleProducts().stream()
                .filter(product -> product.getListingDate().equals(start)
                        || (product.getListingDate().isAfter(start) && product.getListingDate().isBefore(end))
                        || product.getListingDate().equals(end))
                .toList();
    }

    @Override
    public void cancelAllPendingAndAcceptedOrders(WholeSaleProduct wholeSaleProduct) {
        List<WholeSaleOrder> pendingOrders = wholeSaleProduct.getWholeSaleOrders().stream()
                .filter(Order::isPending)
                .toList();

        List<WholeSaleOrder> acceptedOrders = wholeSaleProduct.getWholeSaleOrders().stream()
                .filter(Order::isAccepted)
                .toList();

        pendingOrders.forEach(orderItem -> {
            orderItem.setStatus(Order.Status.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
        acceptedOrders.forEach(orderItem -> {
            orderItem.setStatus(Order.Status.CANCELLED);
            orderItem.setUpdatedAt(LocalDateTime.now());
        });
    }
}
