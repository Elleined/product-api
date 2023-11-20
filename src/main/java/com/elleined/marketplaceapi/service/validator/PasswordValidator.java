package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class PasswordValidator implements Validator<String> {
    @Override
    public void validate(String password) throws WeakPasswordException {
        List<Character> letters = StringUtil.toCharArray(password);

        if (containsWhiteSpace(password)) throw new WeakPasswordException("Password must not contain space!");
        if (containsAtLeastOneUpperCase(letters)) throw new WeakPasswordException("Password must contain at least 1 upper case!");
        if (containsAtLeastOneLowerCase(letters)) throw new WeakPasswordException("Password must contain at least 1 lower case!");
        if (containsAtLeastOneDigit(letters)) throw new WeakPasswordException("Password must contain at least 1 digit!");
        if (containsAtLeastOneSpecialChar(password)) throw new WeakPasswordException("Password must contain at least 1 special character '@', '#', '$', '_', '/'!");
        if (isNLettersLong(password, 8)) throw new WeakPasswordException("Password must be 8 character long!");
    }

    public boolean isPasswordNotMatch(String password, String confirmPassword) {
        return !password.equals(confirmPassword);
    }

    public final boolean containsWhiteSpace(String password) {
        return password.contains(" ");
    }

    public final boolean containsAtLeastOneUpperCase(List<Character> letters) {
        return letters.stream().noneMatch(Character::isUpperCase);
    }

    public final boolean containsAtLeastOneLowerCase(List<Character> letters) {
        return letters.stream().noneMatch(Character::isLowerCase);
    }

    public final boolean containsAtLeastOneDigit(List<Character> letters) {
        return letters.stream().noneMatch(Character::isDigit);
    }

    public final boolean containsAtLeastOneSpecialChar(String password) {
        List<Character> specialChars = Arrays.asList('@', '#', '$', '_', '/');
        return specialChars.stream().noneMatch(c -> password.contains(c.toString()));
    }

    public final boolean isNLettersLong(String password, int preferredPasswordLength) {
        return password.length() < preferredPasswordLength;
    }
}
