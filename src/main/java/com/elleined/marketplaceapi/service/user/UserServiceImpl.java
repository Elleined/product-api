package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ItemRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, SellerService, BuyerService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserMapper userMapper;


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

    private void encodePassword(User user) {
        String rawPassword = user.getUserCredential().getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }


    @Override
    public void updateOrderItemStatus(OrderItem orderItem, OrderItem.OrderItemStatus newOrderItemStatus) {
        final OrderItem.OrderItemStatus oldStatus = orderItem.getOrderItemStatus();
        orderItem.setOrderItemStatus(newOrderItemStatus);
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
                        .map(OrderItem::getProduct))
                .toList();
    }

    @Override
    public void orderProduct(User buyer, Product product) {

    }

    @Override
    public List<Product> getAllOrderedProductsByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return currentUser.getOrderedItems().stream()
                .filter(orderItem -> orderItem.getOrderItemStatus() == orderItemStatus)
                .map(OrderItem::getProduct)
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .toList();
    }

    @Override
    public void deleteOrderItem(User buyer, OrderItem orderItem) {
        Product orderedProduct = orderItem.getProduct();
        buyer.getOrderedItems().remove(orderItem);
        userRepository.save(buyer);
        log.debug("Buyer with id of {} removed his order in product with id of {}", buyer.getId(), orderedProduct.getId());
    }

    @Override
    public List<Product> getAllProductByState(User currentUser, Product.State state) {
        return currentUser.getProducts().stream()
                .filter(product -> product.getStatus() == Product.Status.ACTIVE)
                .filter(product -> product.getState() == state)
                .toList();
    }
}
