package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@Primary
public class MarketplaceUserServiceImpl implements MarketplaceUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User save(UserDTO userDTO) {
//        User user = userMapper.toEntity(userDTO);
//        userRepository.save(user);
//        log.debug("User with name of {} saved successfully! with id of {}", user.getFirstName(), user.getId());
//        return user;
        return null;
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id of " + id + " does not exists!"));
    }


    @Override
    public boolean existsById(int id) {
        return userRepository.existsById(id);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void deleteById(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(int id, UserDTO userDTO) throws ResourceNotFoundException {
        User user = getById(id);
        // userMapper.toUpdate(userDTO, user);
        userRepository.save(user);
    }
}
