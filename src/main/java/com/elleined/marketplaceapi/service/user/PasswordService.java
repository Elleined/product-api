package com.elleined.marketplaceapi.service.user;

public interface PasswordService<T> {
    void encodePassword(T t, String rawPassword);
    void changePassword(T t, String newPassword);
}
