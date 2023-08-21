package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public List<User> getAllUnverifiedUser() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .toList();
    }

    @Override
    public List<Product> getAllPendingProduct() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == Product.State.PENDING)
                .toList();
    }

    @Override
    public void verifiedUser(User userToBeVerified) {
        userToBeVerified.getUserVerification().setStatus(UserVerification.Status.VERIFIED);
        userRepository.save(userToBeVerified);

        log.debug("User with id of {} are now verified", userToBeVerified.getId());
    }

    @Override
    public void verifiedAllUser(List<User> usersToBeVerified) {
        usersToBeVerified.forEach(user -> user.getUserVerification().setStatus(UserVerification.Status.VERIFIED));
        userRepository.saveAll(usersToBeVerified);

        List<Integer> ids = usersToBeVerified.stream().map(User::getId).toList();
        log.debug("Users with id of {} are now verified", ids);
    }

    @Override
    public void listProduct(Product product) {
        product.setState(Product.State.LISTING);
        productRepository.save(product);

        log.debug("Product with id of {} are now listing", product.getId());
    }

    @Override
    public void listAllProduct(List<Product> products) {
        products.forEach(product -> product.setState(Product.State.LISTING));
        productRepository.saveAll(products);

        List<Integer> ids = products.stream().map(Product::getId).toList();
        log.debug("Products with id of {} are now listing", ids);
    }
}
