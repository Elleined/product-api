package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.CartItemDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.*;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, SellerService, BuyerService {
    private static final int REGISTRATION_LIMIT_PROMO = 500;
    private static final BigDecimal REGISTRATION_REWARD = new BigDecimal(50);


    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final PrincipalService principalService;
    private final UserMapper userMapper;

    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemMapper itemMapper;

    private final ProductService productService;

    private final ShopRepository shopRepository;

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id of " + id + " does not exists!"));
    }

    @Override
    public CartItem getByProduct(User buyer, Product product) {
        return buyer.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().equals(product))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id of " + buyer.getId() + " cart item doesn't have product with id of " + product.getId()));
    }


    @Override
    public User saveByDTO(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        String rawPassword = user.getUserCredential().getPassword();
        this.encodePassword(user, rawPassword);

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
    public boolean isSellerExceedsToMaxListingPerDay(User seller) {
        final LocalDateTime currentDateTimeMidnight = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        final LocalDateTime tomorrowMidnight = currentDateTimeMidnight.plusDays(1);
        return productRepository.fetchSellerProductListingCount(currentDateTimeMidnight, tomorrowMidnight, seller) >= SELLER_MAX_LISTING_PER_DAY;
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

    @Override
    public Product saveProduct(ProductDTO productDTO, User seller) {
        Product product = productMapper.toEntity(productDTO, seller);
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
        product.getOrders().forEach(orderItem -> orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED));
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
    public void updateProductStateToSold(User seller, Product product) {
        product.setState(Product.State.SOLD);
        List<CartItem> cartItems = product.getAddedToCarts();
        cartItemRepository.deleteAll(cartItems);

        List<OrderItem> orderItems = product.getOrders();
        updatePendingAndAcceptedOrderStatusToSold(orderItems);
        orderItemRepository.saveAll(orderItems);

        productRepository.save(product);
    }

    private void updatePendingAndAcceptedOrderStatusToSold(List<OrderItem> orderItems) {
        orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
                .forEach(orderItem -> orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.SOLD));
        orderItems.stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
                .forEach(orderItem -> orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.SOLD));
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
    public boolean isBuyerHasPendingOrderToProduct(User buyer, Product product) {
       return buyer.getOrderedItems().stream()
               .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.PENDING)
               .map(OrderItem::getProduct)
               .anyMatch(product::equals);
    }

    @Override
    public boolean isBuyerHasAcceptedOrderToProduct(User buyer, Product product) {
        return buyer.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == OrderItem.OrderItemStatus.ACCEPTED)
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

    @Override
    public void encodePassword(User user, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }

    @Override
    public void changePassword(int userId, String newPassword) throws ResourceNotFoundException, IllegalCallerException {
        User user = getById(userId);
        if (!principalService.getPrincipal().equals(user)) throw new IllegalCallerException("You cannot change the password of other user bruh");
        this.encodePassword(user, newPassword);
        userRepository.save(user);
        log.debug("User with id of {} successfully changed his/her password", user.getId());
    }


    @Override
    public List<CartItem> getAll(User currentUser) {
        return currentUser.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getStatus() == Product.Status.ACTIVE)
                .filter(cartItem -> cartItem.getProduct().getState() == Product.State.LISTING)
                .sorted(Comparator.comparing(CartItem::getOrderDate).reversed())
                .toList();
    }

    @Override
    public void delete(User currentUser, int id) throws ResourceNotFoundException {
        CartItem cartItem = currentUser.getCartItems().stream()
                .filter(item -> item.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id of " + currentUser.getId() + " doesn't have cart item with id of " + id));
        cartItemRepository.delete(cartItem);
        log.debug("User with id of {} successfully deleted cart item with id of {}", currentUser.getId(), id);
    }

    @Override
    public void delete(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
        log.debug("Cart item with id of {} are deleted because user ordered this product with id of {}", cartItem.getId(), cartItem.getProduct().getId());
    }

    @Override
    public CartItem save(User currentUser, CartItemDTO cartItemDTO) {
        CartItem cartItem = itemMapper.toCartItemEntity(cartItemDTO, currentUser);

        double price = productService.calculateOrderPrice(cartItem.getProduct(), cartItem.getOrderQuantity());
        cartItem.setPrice(price);

        currentUser.getCartItems().add(cartItem);
        cartItemRepository.save(cartItem);
        log.debug("User with id of {} added to successfully added to cart product with id of {} and now has cart item id of {}", currentUser.getId(), cartItemDTO.getProductId(), cartItem.getId());
        return cartItem;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public OrderItem moveToOrderItem(CartItem cartItem) {
        OrderItem orderItem = itemMapper.cartItemToOrderItem(cartItem);

        int cartItemId = cartItem.getId();
        cartItemRepository.delete(cartItem);
        orderItemRepository.save(orderItem);
        log.debug("Cart item with id of {} are now moved to order item with id of {}", cartItemId, orderItem.getId());
        return orderItem;
    }

    @Override
    public List<OrderItem> moveAllToOrderItem(List<CartItem> cartItems) {
        List<OrderItem> orderItems = cartItems.stream()
                .map(itemMapper::cartItemToOrderItem)
                .toList();
        return orderItemRepository.saveAll(orderItems);
    }

    @Override
    public boolean isProductAlreadyInCart(User buyer, Product product) {
        return buyer.getCartItems().stream()
                .map(CartItem::getProduct)
                .anyMatch(product::equals);
    }

    @Override
    public CartItem getCartItemById(int cartItemId) throws ResourceNotFoundException {
        return cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Cart item with id of " + cartItemId + " does not exists!"));
    }
}
