package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.field.password.PasswordException;

public interface PasswordService<T> {
    void encodePassword(T t, String rawPassword);
    void changePassword(T t, String newPassword, String retypeNewPassword)
            throws PasswordException;

    default boolean isTwoPasswordNotMatch(String password, String anotherPassword) {
        return !password.equals(anotherPassword);
    }
}
