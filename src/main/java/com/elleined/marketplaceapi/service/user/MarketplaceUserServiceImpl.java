package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@Qualifier("marketplaceUserService")
public class MarketplaceUserServiceImpl implements MarketplaceUserService {

    @Override
    public User save(UserDTO userDTO) {
        return null;
    }

    @Override
    public void save(User user) {

    }

    @Override
    public User getById(int userId) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public User getByUUID(String uuid) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean existsById(int id) {
        return false;
    }

    @Override
    public void delete(User user) {

    }

    @Override
    public void deleteById(int id) {

    }

    @Override
    public User update(UserDTO userDTO) {
        return null;
    }
}
