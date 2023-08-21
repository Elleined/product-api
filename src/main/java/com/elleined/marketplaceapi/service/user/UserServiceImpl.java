package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.item.OrderItemDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.ItemMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ItemRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, SellerService, BuyerService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    private final ProductService productService;

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
    public void update(UserDTO userDTO, User user) throws ResourceNotFoundException {
        User updatedUser = userMapper.toUpdate(userDTO, user);
        userRepository.save(updatedUser);
        log.debug("User with id of {} updated successfully!", updatedUser.getId());
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
        User user = getByEmail(userCredentialDTO.getEmail());
        String encodedPassword = user.getUserCredential().getPassword();

        if (!userRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
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

    private void encodePassword(User user) {
        String rawPassword = user.getUserCredential().getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }


    @Override
    public void updateOrderItemStatus(OrderItem orderItem, OrderItem.OrderItemStatus newOrderItemStatus, String messageToBuyer) {
        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(newOrderItemStatus);
        orderItem.setSellerMessage(messageToBuyer);
        itemRepository.save(orderItem);
        log.debug("Seller successfully updated order item with id of {} status from {} to {}", orderItem.getId(), oldStatus, newOrderItemStatus );
    }

    @Override
    public List<Product> getAllSellerProductOrderByStatus(User seller, OrderItem.OrderItemStatus orderItemStatus) {
        List<Product> sellableProducts = seller.getProducts();
        return sellableProducts.stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .flatMap(product -> product.getOrders().stream()
                        .filter(productOrder -> productOrder.getOrderItemStatus() == orderItemStatus)
                        .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed())
                        .map(OrderItem::getProduct))
                .toList();
    }

    @Override
    public OrderItem orderProduct(User buyer, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = itemMapper.toOrderItemEntity(orderItemDTO, buyer);

        double price = productService.calculatePrice(orderItem.getProduct(), orderItemDTO.getOrderQuantity());
        orderItem.setPrice(price);

        buyer.getOrderedItems().add(orderItem);
        itemRepository.save(orderItem);
        log.debug("User with id of {} successfully ordered product with id of {}", buyer.getId(), orderItem.getProduct().getId());
        return orderItem;
    }

    @Override
    public List<Product> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return currentUser.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == orderItemStatus)
                .sorted(Comparator.comparing(OrderItem::getOrderDate).reversed())
                .map(OrderItem::getProduct)
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .toList();
    }

    @Override
    public void cancelOrderItem(User buyer, OrderItem orderItem) {
        orderItem.setOrderItemStatus(OrderItem.OrderItemStatus.CANCELLED);
        itemRepository.save(orderItem);
        log.debug("Buyer with id of {} cancel his order in product with id of {}", buyer.getId(), orderItem.getProduct().getId());
    }

    @Override
    public List<Product> getAllProductByState(User currentUser, Product.State state) {
        return currentUser.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == state)
                .toList();
    }
}
