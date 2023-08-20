package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.user.User;
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
    public boolean isExists(User user) {
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
    public void update(int id, UserDTO userDTO) throws ResourceNotFoundException {

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
    public Product getAllOrder(User currentUser) {
        return null;
    }
}
