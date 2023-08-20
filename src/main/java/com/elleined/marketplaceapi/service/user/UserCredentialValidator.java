package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.AlreadExistException;
import com.elleined.marketplaceapi.exception.MalformedEmailException;
import com.elleined.marketplaceapi.exception.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.WeakPasswordException;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCredentialValidator {
    private final UserService userService;

    public void validateEmail(UserDTO.UserCredentialDTO userCredentialDTO) throws MalformedEmailException, AlreadExistException {
        String email = userCredentialDTO.getEmail();
        if (email.endsWith("@gmail.com") || email.startsWith("@")) throw new MalformedEmailException("Email must ends with @gmail.com and should not starts with @!");
        if (userService.getAllEmail().contains(email)) throw new AlreadExistException("This email is already associated with an account!");
    }

    public void validatePassword(UserDTO.UserCredentialDTO userCredentialDTO) throws PasswordNotMatchException, WeakPasswordException {
        List<Character> specialChars = Arrays.asList('@', '#', '$', '_', '/');
        String password = userCredentialDTO.getPassword();
        List<Character> letters = StringUtil.toCharArray(password);
        String confirmPassword = userCredentialDTO.getConfirmPassword();

        if (!password.equals(confirmPassword)) throw new PasswordNotMatchException("Password not match!");
        if (password.contains(" ")) throw new WeakPasswordException("Password must not contain space!");
        if (letters.stream().anyMatch(Character::isUpperCase)) throw new WeakPasswordException("Password must contain at least 1 upper case!");
        if (letters.stream().anyMatch(Character::isLowerCase)) throw new WeakPasswordException("Password must contain at least 1 lower case!");
        if (letters.stream().anyMatch(Character::isDigit)) throw new WeakPasswordException("Password must contain at least 1 digit!");
        if (specialChars.stream().anyMatch(c -> password.contains(c.toString()))) throw new WeakPasswordException("Password must contain at least 1 special character '@', '#', '$', '_', '/'!");
        if (password.length() < 8) throw new WeakPasswordException("Password must be 8 character long!");
    }
}
