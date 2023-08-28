package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.user.User;
public interface PasswordService<T> {
    void encodePassword(T t, String rawPassword);
    void changePassword(T t, String newPassword);
}
