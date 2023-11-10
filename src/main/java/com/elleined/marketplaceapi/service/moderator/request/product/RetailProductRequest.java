package com.elleined.marketplaceapi.service.moderator.request.product;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.RetailProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.product.RetailProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
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
@Primary
public class RetailProductRequest implements ProductRequest<RetailProduct> {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    private final ModeratorRepository moderatorRepository;

    private final RetailProductRepository retailProductRepository;

    @Override
    public List<RetailProduct> getAllRequest() {
        List<RetailProduct> premiumUserRetailProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getRetailProducts)
                .flatMap(products -> products.stream()
                        .filter(Product::isNotDeleted)
                        .filter(Product::isPending))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<RetailProduct> regularUserRetailProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getRetailProducts)
                .flatMap(products -> products.stream()
                        .filter(Product::isNotDeleted)
                        .filter(Product::isPending))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<RetailProduct> products = new ArrayList<>();
        products.addAll(premiumUserRetailProducts);
        products.addAll(regularUserRetailProducts);
        return products;
    }

    @Override
    public void accept(Moderator moderator, RetailProduct retailProduct) {
        retailProduct.setState(Product.State.LISTING);
        moderator.addListedProducts(retailProduct);

        moderatorRepository.save(moderator);
        retailProductRepository.save(retailProduct);

        log.debug("Product with id of {} are now listing", retailProduct.getId());
    }

    @Override
    public void acceptAll(Moderator moderator, Set<RetailProduct> retailProducts) {
        retailProducts.forEach(retailProduct -> this.accept(moderator, retailProduct));
        log.debug("Products with id of {} are now listing", retailProducts.stream().map(Product::getId).toList());
    }

    @Override
    public void reject(Moderator moderator, RetailProduct retailProduct) {
        retailProduct.setState(Product.State.REJECTED);
        moderator.addRejectedProduct(retailProduct);

        moderatorRepository.save(moderator);
        retailProductRepository.save(retailProduct);

        log.debug("Product with id of {} are rejected by moderator with id of {}", retailProduct.getId(), moderator.getId());
    }

    @Override
    public void rejectAll(Moderator moderator, Set<RetailProduct> retailProducts) {
        retailProducts.forEach(retailProduct -> this.reject(moderator, retailProduct));
        log.debug("Products with ids of {} rejected successfully", retailProducts.stream().map(Product::getId).toList());
    }
}
