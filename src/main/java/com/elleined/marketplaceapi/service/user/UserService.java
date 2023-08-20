package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

// other related user marketplace here
public interface UserService {

    User saveByDTO(UserDTO dto);

    User getById(int id) throws ResourceNotFoundException;
    boolean existsById(int id);

    List<User> getAll();

    void update(User user, UserDTO userDTO);
    void delete(int id) throws ResourceNotFoundException;

    Address getAddress(User currentUser);
    List<Address> getAllDeliveryAddress(User currentUser);
    List<Product> getAllOrderItemByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus);
    List<Product> getAllCartItemByStatus(User currentUser, OrderItem.OrderItemStatus orderItemStatus);

    boolean hasProduct(User currentUser, Product product);

    boolean isVerified(User currentUser);
}
