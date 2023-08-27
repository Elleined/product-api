package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import com.elleined.marketplaceapi.exception.field.password.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.service.GetAllUtilityService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserCredentialValidator {
    private final GetAllUtilityService getAllUtilityService;

    public void validateEmail(UserDTO.UserCredentialDTO userCredentialDTO)
            throws MalformedEmailException, AlreadyExistException {
        String email = userCredentialDTO.getEmail();
        if (!email.endsWith("@gmail.com")) throw new MalformedEmailException("Email must ends with @gmail.com!");
        if (email.startsWith("@")) throw new MalformedEmailException("Email should not starts with @!");
        if (getAllUtilityService.getAllEmail().contains(email)) throw new AlreadyExistException("This email " + email + " is already associated with an account!");
    }

    public void validatePassword(UserDTO.UserCredentialDTO userCredentialDTO)
            throws PasswordNotMatchException, WeakPasswordException {
        List<Character> specialChars = Arrays.asList('@', '#', '$', '_', '/');
        String password = userCredentialDTO.getPassword();
        List<Character> letters = StringUtil.toCharArray(password);
        String confirmPassword = userCredentialDTO.getConfirmPassword();

        if (!password.equals(confirmPassword)) throw new PasswordNotMatchException("Password not match!");
        if (password.contains(" ")) throw new WeakPasswordException("Password must not contain space!");
        if (letters.stream().noneMatch(Character::isUpperCase)) throw new WeakPasswordException("Password must contain at least 1 upper case!");
        if (letters.stream().noneMatch(Character::isLowerCase)) throw new WeakPasswordException("Password must contain at least 1 lower case!");
        if (letters.stream().noneMatch(Character::isDigit)) throw new WeakPasswordException("Password must contain at least 1 digit!");
        if (specialChars.stream().noneMatch(c -> password.contains(c.toString()))) throw new WeakPasswordException("Password must contain at least 1 special character '@', '#', '$', '_', '/'!");
        if (password.length() < 8) throw new WeakPasswordException("Password must be 8 character long!");
    }
}
