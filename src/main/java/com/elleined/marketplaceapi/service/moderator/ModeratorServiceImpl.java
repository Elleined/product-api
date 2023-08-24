package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.NotVerifiedException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final UserService userService;

    private final UserMapper userMapper;
    private final ProductMapper productMapper;


    @Override
    public List<UserDTO> getAllUnverifiedUser() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(user -> user.getShop() != null)
                .map(userMapper::toDTO)
                .toList();
    }

    @Override

    public List<ProductDTO> getAllPendingProduct() {
        return userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.VERIFIED)
                .filter(user -> user.getShop() != null)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.PENDING)
                        .map(productMapper::toDTO))
                .toList();
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = MessagingException.class
    )
    public void verifyUser(int userToBeVerifiedId) throws ResourceNotFoundException {
        User userToBeVerified = userRepository.findById(userToBeVerifiedId).orElseThrow(() -> new ResourceNotFoundException("User with id of " + userToBeVerifiedId + " does not exists!"));
        if (userToBeVerified.getShop() == null) throw new NotVerifiedException("User with id of " + userToBeVerifiedId + " doesn't have pending shop registration! must send a shop registration first!");

        if (userService.isLegibleForRegistrationPromo()) userService.availRegistrationPromo(userToBeVerified);
        userToBeVerified.getUserVerification().setStatus(UserVerification.Status.VERIFIED);
        userRepository.save(userToBeVerified);

        emailService.sendVerificationEmail(userToBeVerified);
        log.debug("User with id of {} are now verified", userToBeVerified.getId());
    }

    @Override
    public void verifyAllUser(List<Integer> userToBeVerifiedIds) throws ResourceNotFoundException {
        userToBeVerifiedIds.forEach(this::verifyUser);
        log.debug("Users with id of {} are now verified", userToBeVerifiedIds);
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = MessagingException.class
    )
    public void listProduct(int productId) throws ResourceNotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id of " + productId + " does not exists!"));
        product.setState(Product.State.LISTING);
        productRepository.save(product);

        emailService.sendProductEmail(product.getSeller(), product);
        log.debug("Product with id of {} are now listing", product.getId());
    }

    @Override
    public void listAllProduct(List<Integer> productIds) throws ResourceNotFoundException {
        productIds.forEach(this::listProduct);
        log.debug("Products with id of {} are now listing", productIds);
    }
}
