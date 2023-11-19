package com.elleined.marketplaceapi.service.password;

import com.elleined.marketplaceapi.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordEncoder implements EntityPasswordEncoder<User> {
    private final PasswordEncoder passwordEncoder;
    @Override
    public void encodePassword(User user, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }

    @Override
    public boolean matches(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getUserCredential().getPassword());
    }
}
