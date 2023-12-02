package com.elleined.marketplaceapi.service.product.wholesale;

import com.elleined.marketplaceapi.dto.product.sale.request.SaleWholeSaleRequest;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.order.Order;
import com.elleined.marketplaceapi.model.order.WholeSaleOrder;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.order.WholeSaleOrderRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import com.elleined.marketplaceapi.utils.Formatter;
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
    private final WholeSaleOrderRepository wholeSaleOrderRepository;
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
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
                .toList();

        List<WholeSaleProduct> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremiumAndNotExpired())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getWholeSaleProducts)
                .flatMap(Collection::stream)
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
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
                .filter(Product::isNotDeleted)
                .filter(product -> product.getState() == state)
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();
    }

    @Override
    public List<WholeSaleProduct> searchProductByCropName(String cropName) {
        return wholeSaleProductRepository.searchProductByCropName(cropName).stream()
                .filter(Product::isNotDeleted)
                .filter(Product::isListed)
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
    public void updateAllPendingAndAcceptedOrders(WholeSaleProduct wholeSaleProduct, Order.Status status) {
        List<WholeSaleOrder> pendingOrders = wholeSaleProduct.getWholeSaleOrders().stream()
                .filter(Order::isPending)
                .toList();

        List<WholeSaleOrder> acceptedOrders = wholeSaleProduct.getWholeSaleOrders().stream()
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
        wholeSaleOrderRepository.saveAll(pendingOrders);
        wholeSaleOrderRepository.saveAll(acceptedOrders);
    }

    @Override
    public boolean isRejectedBySeller(User buyer, WholeSaleProduct wholeSaleProduct) {
        return buyer.getWholeSaleOrders().stream()
                .filter(wholeSaleOrder -> wholeSaleOrder.getWholeSaleProduct().equals(wholeSaleProduct))
                .anyMatch(wholeSaleOrder -> {
                    LocalDateTime reOrderingDate = wholeSaleOrder.getUpdatedAt().plusDays(1);
                    return wholeSaleOrder.isRejected() && (LocalDateTime.now().equals(reOrderingDate) || LocalDateTime.now().isBefore(reOrderingDate));
                });
    }

    @Override
    public double calculateSalePrice(SaleWholeSaleRequest saleWholeSaleRequest) {
        return Formatter.formatDouble((saleWholeSaleRequest.getSalePrice() * (saleWholeSaleRequest.getSalePercentage() / 100f)));
    }

    @Override
    public double calculateSalePrice(double totalPrice, int salePercentage) {
        return Formatter.formatDouble((totalPrice * (salePercentage / 100f)));
    }
}
