package com.elleined.marketplaceapi.service.moderator.request.product;

import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.product.WholeSaleProduct;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.repository.product.WholeSaleProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
@Qualifier("wholeSaleProductRequest")
public class WholeSaleProductRequest implements ProductRequest<WholeSaleProduct> {
    private final UserRepository userRepository;
    private final PremiumRepository premiumRepository;

    private final ModeratorRepository moderatorRepository;

    private final WholeSaleProductRepository wholeSaleProductRepository;

    @Override
    public List<WholeSaleProduct> getAllRequest() {
        List<WholeSaleProduct> premiumUserWholeSaleProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getWholeSaleProducts)
                .flatMap(products -> products.stream()
                        .filter(Product::isNotDeleted)
                        .filter(Product::isPending))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<WholeSaleProduct> regularUserWholeSaleProducts = userRepository.findAll().stream()
                .filter(user -> !user.isPremium())
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .map(User::getWholeSaleProducts)
                .flatMap(products -> products.stream()
                        .filter(Product::isNotDeleted)
                        .filter(Product::isPending))
                .sorted(Comparator.comparing(Product::getListingDate).reversed())
                .toList();

        List<WholeSaleProduct> products = new ArrayList<>();
        products.addAll(premiumUserWholeSaleProducts);
        products.addAll(regularUserWholeSaleProducts);
        return products;
    }

    @Override
    public void accept(Moderator moderator, WholeSaleProduct wholeSaleProduct) {
        wholeSaleProduct.setState(Product.State.LISTING);
        moderator.addListedProducts(wholeSaleProduct);

        moderatorRepository.save(moderator);
        wholeSaleProductRepository.save(wholeSaleProduct);

        log.debug("Product with id of {} are now listing", wholeSaleProduct.getId());
    }

    @Override
    public void acceptAll(Moderator moderator, Set<WholeSaleProduct> wholeSaleProducts) {
        wholeSaleProducts.forEach(wholeSaleProduct -> this.accept(moderator, wholeSaleProduct));
        log.debug("Products with id of {} are now listing", wholeSaleProducts.stream().map(Product::getId).toList());
    }

    @Override
    public void reject(Moderator moderator, WholeSaleProduct wholeSaleProduct) {
        wholeSaleProduct.setState(Product.State.REJECTED);
        moderator.addRejectedProduct(wholeSaleProduct);

        moderatorRepository.save(moderator);
        wholeSaleProductRepository.save(wholeSaleProduct);

        log.debug("Product with id of {} are rejected by moderator with id of {}", wholeSaleProduct.getId(), moderator.getId());
    }

    @Override
    public void rejectAll(Moderator moderator, Set<WholeSaleProduct> wholeSaleProducts) {
        wholeSaleProducts.forEach(wholeSaleProduct -> this.reject(moderator, wholeSaleProduct));
        log.debug("Products with ids of {} rejected successfully", wholeSaleProducts.stream().map(Product::getId).toList());
    }
}
