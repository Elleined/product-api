package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
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
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
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
    public void update(User user, UserDTO userDTO) throws ResourceNotFoundException {

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
    public int login(String email, String password) throws ResourceNotFoundException, InvalidUserCredentialException {
        User user = getByEmail(email);
        String encodedPassword = user.getUserCredential().getPassword();
        if (!passwordEncoder.matches(password, encodedPassword)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        return user.getId();
    }

    @Override
    public User getByEmail(String email) throws ResourceNotFoundException {
        return userRepository.fetchByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email of " + email + " does not exists!"));
    }

    @Override
    public List<String> getAllEmail() {
        return userRepository.fetchAllEmail();
    }

    private void encodePassword(User user) {
        String rawPassword = user.getUserCredential().getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }
}
