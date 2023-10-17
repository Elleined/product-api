package com.elleined.marketplaceapi.service.moderator.request;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductRequest implements Request<Product> {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    private final ModeratorRepository moderatorRepository;

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllRequest() {
        List<Product> premiumUserProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.PENDING))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<Product> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.PENDING))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<Product> products = new ArrayList<>();
        products.addAll(premiumUserProducts);
        products.addAll(regularUserProducts);
        return products;
    }

    @Override
    public void accept(Moderator moderator, Product productToBeListed) {
        productToBeListed.setState(Product.State.LISTING);
        moderator.addListedProducts(productToBeListed);

        moderatorRepository.save(moderator);
        productRepository.save(productToBeListed);

        log.debug("Product with id of {} are now listing", productToBeListed.getId());
    }

    @Override
    public void acceptAll(Moderator moderator, Set<Product> productsToBeListed) {
        productsToBeListed.forEach(product -> this.accept(moderator, product));
        log.debug("Products with id of {} are now listing", productsToBeListed.stream().map(Product::getId).toList());
    }

    @Override
    public void reject(Moderator moderator, Product productToBeRejected) {
        productToBeRejected.setState(Product.State.REJECTED);
        moderator.addRejectedProduct(productToBeRejected);

        moderatorRepository.save(moderator);
        productRepository.save(productToBeRejected);

        log.debug("Product with id of {} are rejected by moderator with id of {}", productToBeRejected.getId(), moderator.getId());
    }

    @Override
    public void rejectAll(Moderator moderator, Set<Product> productsToBeRejected) {
        productsToBeRejected.forEach(productToBeRejected -> this.reject(moderator, productToBeRejected));
        log.debug("Products with ids of {} rejected successfully", productsToBeRejected.stream().map(Product::getId).toList());
    }
}
