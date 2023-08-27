package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.user.User;
public interface PasswordService {
    void encodePassword(User user, String rawPassword);
    void changePassword(int userId, String newPassword) throws ResourceNotFoundException;
}
