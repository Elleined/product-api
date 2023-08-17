package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;

public interface UserService {
    User save(UserDTO userDTO);
    void save(User user);
    User getById(int userId) throws ResourceNotFoundException;
    User getByUUID(String uuid) throws ResourceNotFoundException;
    boolean existsById(int id);
    void delete(User user);
    void deleteById(int id);
    User update(UserDTO userDTO);
}
