package com.elleined.marketplaceapi.service.password;

import com.elleined.marketplaceapi.model.Moderator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModeratorPasswordEncoder implements EntityPasswordEncoder<Moderator> {
    private final PasswordEncoder passwordEncoder;
    @Override
    public void encodePassword(Moderator moderator, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        moderator.getModeratorCredential().setPassword(encodedPassword);
    }

    @Override
    public boolean matches(Moderator moderator, String rawPassword) {
        return passwordEncoder.matches(rawPassword, moderator.getModeratorCredential().getPassword());
    }
}
