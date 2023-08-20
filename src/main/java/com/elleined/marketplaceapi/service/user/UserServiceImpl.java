package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.VerifiedUser;
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
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void delete(User user) {

    }

    @Override
    public void delete(int id) {

    }

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isExists(int id) {
        return false;
    }


    @Override
    public User saveByDTO(UserDTO userDTO) {
        return null;
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public void update(User user, UserDTO userDTO) throws ResourceNotFoundException {

    }

    @Override
    public VerifiedUser getVerifiedUser(int id) {
        return null;
    }

    @Override
    public Address getAddress(User currentUser) {
        return null;
    }

    @Override
    public List<Address> getAllDeliveryAddress(User currentUser) {
        return null;
    }

    @Override
    public List<Product> getAllOrderItemByStatus(VerifiedUser currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return null;
    }

    @Override
    public List<Product> getAllCartItemByStatus(VerifiedUser currentUser, OrderItem.OrderItemStatus orderItemStatus) {
        return null;
    }

    @Override
    public boolean hasProduct(VerifiedUser currentUser, Product product) {
        return currentUser.getProducts().stream().anyMatch(product::equals);
    }
}
