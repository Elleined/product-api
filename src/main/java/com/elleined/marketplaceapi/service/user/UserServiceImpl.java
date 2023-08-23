package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.InsufficientBalanceException;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.repository.ProductRepository;
import com.elleined.marketplaceapi.repository.ShopRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, SellerService, BuyerService {
    private static final int REGISTRATION_LIMIT_PROMO = 500;
    private static final float LISTING_FEE_PERCENTAGE = 5;
    private static final BigDecimal REGISTRATION_REWARD = new BigDecimal(50);

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final OrderItemRepository orderItemRepository;
    private final ItemMapper itemMapper;

    private final ProductService productService;

    private final ShopRepository shopRepository;

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id of " + id + " does not exists!"));
    }


    @Override
    public User saveByDTO(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        encodePassword(user);
        userRepository.save(user);
        log.debug("User with name of {} saved successfully with id of {}", user.getUserDetails().getFirstName(), user.getId());
        return user;
    }

    @Override
    public boolean hasProduct(User currentUser, Product product) {
        return currentUser.getProducts().stream().anyMatch(product::equals);
    }

    @Override
    public boolean isVerified(User currentUser) {
        return currentUser.getUserVerification().getStatus() == UserVerification.Status.VERIFIED;
    }

    @Override
    public void resendValidId(User currentUser, String validId) {
        currentUser.getUserVerification().setValidId(validId);
        userRepository.save(currentUser);
        log.debug("User with id of {} resended valid id {}", currentUser.getId(), validId);
    }

    @Override
    public User login(UserDTO.UserCredentialDTO userCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException {
        String email = userCredentialDTO.getEmail();
        if (!userRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");

        User user = getByEmail(userCredentialDTO.getEmail());
        String encodedPassword = user.getUserCredential().getPassword();
        if (!passwordEncoder.matches(userCredentialDTO.getPassword(), encodedPassword)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        return user;
    }

    @Override
    public User getByEmail(String email) throws ResourceNotFoundException {
        return userRepository.fetchByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email of " + email + " does not exists!"));
    }

    @Override
    public List<String> getAllEmail() {
        return userRepository.fetchAllEmail();
    }

    @Override
    public List<String> getAllMobileNumber() {
        return userRepository.fetchAllMobileNumber();
    }

    @Override
    public boolean existsById(int userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public boolean isBalanceNotEnoughToPayListingFee(User seller, double listingFee) {
        return seller.getBalance().compareTo(new BigDecimal(listingFee)) <= 0;
    }

    @Override
    public double getListingFee(double productTotalPrice) {
        return (productTotalPrice * (LISTING_FEE_PERCENTAGE / 100f));
    }

    @Override
    public double getTotalPrice(ProductDTO productDTO) {
        return productDTO.getPricePerUnit() * productDTO.getQuantityPerUnit();
    }

    @Override
    public boolean isLegibleForRegistrationPromo() {
        return userRepository.findAll().size() <= REGISTRATION_LIMIT_PROMO;
    }

    @Override
    public void availRegistrationPromo(User registratingUser) {
        BigDecimal newBalance = registratingUser.getBalance().add(REGISTRATION_REWARD);
        registratingUser.setBalance(newBalance);
        userRepository.save(registratingUser);
        log.debug("Registrating user receives {} as registration reward for the first {} users", REGISTRATION_REWARD, REGISTRATION_LIMIT_PROMO);
    }

    @Override
    public void sendShopRegistration(User owner, ShopDTO shopDTO) {
        Shop shop = Shop.builder()
                .picture(shopDTO.getPicture())
                .name(shopDTO.getShopName())
                .description(shopDTO.getDescription())
                .owner(owner)
                .build();
        owner.getUserVerification().setValidId(shopDTO.getValidId());

        userRepository.save(owner);
        shopRepository.save(shop);
        log.debug("Shop registration of owner with id of {} success his verification are now visible in moderator", owner.getId());
    }

    @Override
    public boolean isUserHasShopRegistration(User user) {
        return user.getShop() != null;
    }

    @Override
    public OrderItem getOrderItemById(int orderItemId) throws ResourceNotFoundException {
        return orderItemRepository.findById((long) orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order item with id of " + orderItemId + " does not exists!"));
    }

    @Override
    public User getByReferralCode(String referralCode) throws ResourceNotFoundException {
        return userRepository.fetchByReferralCode(referralCode).orElseThrow(() -> new ResourceNotFoundException("User with referral code of " + referralCode +  " does not exists!"));
    }

    @Override
    public void addInvitedUser(String invitingUserReferralCode, User invitedUser) {
        User invitingUser = getByReferralCode(invitingUserReferralCode);
        invitingUser.getReferredUsers().add(invitedUser);
        userRepository.save(invitingUser);
        log.debug("User with id of {} invited user with id of {} successfully", invitingUser.getId(), invitedUser.getId());
    }

    private void encodePassword(User user) {
        String rawPassword = user.getUserCredential().getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }


    @Override
    public Product saveProduct(ProductDTO productDTO) {
        Product product = productMapper.toEntity(productDTO);
        productRepository.save(product);
        log.debug("Product saved successfully with id of {}", product.getId());
        return product;
    }

    @Override
    public void updateProduct(Product product, ProductDTO productDTO) {
        Product updatedProduct = productMapper.toUpdate(product, productDTO);
        productRepository.save(updatedProduct);
        log.debug("Product with id of {} updated successfully!", updatedProduct.getId());
    }

    @Override
    public void deleteProduct(int productId) throws ResourceNotFoundException {
        Product product = productService.getById(productId);
        product.setStatus(Product.Status.INACTIVE);
        productRepository.save(product);

        log.debug("Product with id of {} are now inactive", product.getId());
    }

    @Override
    public void acceptOrder(User seller, OrderItem orderItem, String messageToBuyer) {
        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.ACCEPTED);
        orderItem.setSellerMessage(messageToBuyer);
        orderItemRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.ACCEPTED.name());
    }


    @Override
    public void rejectOrder(User seller, OrderItem orderItem, String messageToBuyer) {
        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.REJECTED);
        orderItem.setSellerMessage(messageToBuyer);
        orderItemRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, OrderItem.OrderItemStatus.REJECTED.name());
    }

    @Override
    public List<OrderItem> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus) {
        return seller.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .flatMap(product -> product.getOrders().stream()
                        .filter(productOrder -> productOrder.getOrderItemStatus() == orderItemStatus)
                        .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed()))
                .toList();
    }

    @Override
    public boolean isSellerHasOrder(User seller, OrderItem orderItem) {
        return seller.getProducts().stream()
                .map(Product::getOrders)
                .flatMap(Collection::stream)
                .anyMatch(orderItem::equals);
    }

    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = itemMapper.toOrderItemEntity(orderItemDTO, buyer);

        double price = productService.calculateOrderPrice(orderItem.getProduct(), orderItemDTO.getOrderQuantity());
        orderItem.setPrice(price);

        buyer.getOrderedItems().add(orderItem);
        orderItemRepository.save(orderItem);
        log.debug("User with id of {} successfully ordered product with id of {}", buyer.getId(), orderItem.getProduct().getId());
        return orderItem;
    }

    @Override
    public List<OrderItem> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return currentUser.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == orderItemStatus)
                .filter(orderItem -> orderItem.getProduct().getStatus() == Product.Status.ACTIVE)
                .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed())
                .toList();
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem) {
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
        orderItemRepository.save(orderItem);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), orderItem.getProduct().getId());
    }

    @Override
    public boolean isUserAlreadyOrderedProduct(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .map(OrderItem::getProduct)
                .anyMatch(product::equals);
    }

    @Override
    public boolean isBuyerHasOrder(User buyer, OrderItem orderItem) {
        return buyer.getOrderedItems().stream().anyMatch(orderItem::equals);
    }

    @Override
    public boolean isSellerAcceptedOrder(OrderItem orderItem) {
        return orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED;
    }

    @Override
    public List<Product> getAllProductByState(User currentUser, Product.State state) {
        return currentUser.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == state)
                .toList();
    }
}
