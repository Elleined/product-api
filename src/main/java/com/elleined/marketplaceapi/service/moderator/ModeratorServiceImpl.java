package com.elleined.marketplaceapi.service.moderator;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.mapper.ModeratorMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.Premium;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ModeratorRepository;
import com.elleined.marketplaceapi.repository.PremiumRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.fee.FeeService;
import com.elleined.marketplaceapi.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ModeratorServiceImpl implements ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final ModeratorMapper moderatorMapper;

    private final EmailService emailService;

    private final PremiumRepository premiumRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;

    private final FeeService feeService;

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> getAllUnverifiedUser() {
        List<User> premiumUsers = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(user -> user.getShop() != null)
                .toList();

        List<User> regularUsers = userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.NOT_VERIFIED)
                .filter(user -> user.getShop() != null)
                .toList();

        List<User> users = new ArrayList<>();
        users.addAll(premiumUsers);
        users.addAll(regularUsers);
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> getAllPendingProduct() {
        List<Product> premiumUserProducts = premiumRepository.findAll().stream()
                .map(Premium::getUser)
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.VERIFIED)
                .filter(user -> user.getShop() != null)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.PENDING))
                .toList();

        List<Product> regularUserProducts = userRepository.findAll().stream()
                .filter(user -> user.getUserVerification().getStatus() == UserVerification.Status.VERIFIED)
                .filter(user -> user.getShop() != null)
                .map(User::getProducts)
                .flatMap(products -> products.stream()
                        .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                        .filter(product -> product.getState() == Product.State.PENDING))
                .toList();

        List<Product> products = new ArrayList<>();
        products.addAll(premiumUserProducts);
        products.addAll(regularUserProducts);
        return products.stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = MessagingException.class
    )
    public void verifyUser(Moderator moderator, User userToBeVerified) throws NoShopRegistrationException {
        if (userToBeVerified.isVerified()) return;
        if (userToBeVerified.getShop() == null) throw new NoShopRegistrationException("User with id of " + userToBeVerified.getId() + " doesn't have pending shop registration! must send a shop registration first!");

        if (userService.isLegibleForRegistrationPromo()) userService.availRegistrationPromo(userToBeVerified);
        User invitingUser = userService.getInvitingUser(userToBeVerified);
        if (invitingUser != null) feeService.payInvitingUserForHisReferral(invitingUser);
        if (invitingUser != null && feeService.isInvitingUserLegibleForExtraReferralReward(invitingUser)) feeService.payExtraReferralRewardForInvitingUser(invitingUser);

        userToBeVerified.getUserVerification().setStatus(UserVerification.Status.VERIFIED);
        moderator.addVerifiedUser(userToBeVerified);

        userRepository.save(userToBeVerified);
        moderatorRepository.save(moderator);

        emailService.sendVerificationEmail(userToBeVerified);
        log.debug("User with id of {} are now verified", userToBeVerified.getId());
    }

    @Override
    public void verifyAllUser(Moderator moderator, Set<User> usersToBeVerified) throws NoShopRegistrationException {
        usersToBeVerified.forEach(userToBeVerified -> this.verifyUser(moderator, userToBeVerified));
        log.debug("Users with id of {} are now verified", usersToBeVerified.stream().map(User::getId).toList());
    }

    @Override
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = MessagingException.class
    )
    public void listProduct(Moderator moderator, Product productToBeListed) {
        productToBeListed.setState(Product.State.LISTING);
        moderator.addListedProducts(productToBeListed);

        moderatorRepository.save(moderator);
        productRepository.save(productToBeListed);

        emailService.sendProductEmail(productToBeListed.getSeller(), productToBeListed);
        log.debug("Product with id of {} are now listing", productToBeListed.getId());
    }

    @Override
    public void listAllProduct(Moderator moderator, Set<Product> productsToBeListed) {
        productsToBeListed.forEach(product -> this.listProduct(moderator, product));
        log.debug("Products with id of {} are now listing", productsToBeListed.stream().map(Product::getId).toList());
    }

    @Override
    public Moderator getById(int moderatorId) throws ResourceNotFoundException {
        return moderatorRepository.findById(moderatorId).orElseThrow(() -> new ResourceNotFoundException("Moderator with id of " + moderatorId + " does not exists!"));
    }

    @Override
    public Moderator save(ModeratorDTO moderatorDTO) {
        Moderator moderator = moderatorMapper.toEntity(moderatorDTO);
        this.encodePassword(moderator, moderatorDTO.moderatorCredentialDTO().getPassword());
        moderatorRepository.save(moderator);
        log.debug("Moderator name of {} successfully saved with id of {}", moderatorDTO.name(), moderatorDTO.id());
        return moderator;
    }

    @Override
    public ModeratorDTO login(CredentialDTO moderatorCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException {
        String email = moderatorCredentialDTO.getEmail();
        if (!moderatorRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");

        Moderator moderator = moderatorRepository.fetchByEmail(moderatorCredentialDTO.getEmail());
        String encodedPassword = moderator.getModeratorCredential().getPassword();
        if (!passwordEncoder.matches(moderatorCredentialDTO.getPassword(), encodedPassword)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        log.debug("Moderator with id of {} are now logged in", moderator.getId());
        return moderatorMapper.toDTO(moderator);
    }

    @Override
    public void encodePassword(Moderator moderator, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        moderator.getModeratorCredential().setPassword(encodedPassword);
    }

    @Override
    public void changePassword(Moderator moderator, String newPassword) {
        this.encodePassword(moderator, newPassword);
        moderatorRepository.save(moderator);
        log.debug("User with id of {} successfully changed his/her password", moderator.getId());
    }
}
