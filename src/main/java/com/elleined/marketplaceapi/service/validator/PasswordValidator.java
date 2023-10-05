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
        List<Character> specialChars = Arrays.asList('@', '#', '$', '_', '/');
        List<Character> letters = StringUtil.toCharArray(password);

        if (password.contains(" ")) throw new WeakPasswordException("Password must not contain space!");
        if (letters.stream().noneMatch(Character::isUpperCase)) throw new WeakPasswordException("Password must contain at least 1 upper case!");
        if (letters.stream().noneMatch(Character::isLowerCase)) throw new WeakPasswordException("Password must contain at least 1 lower case!");
        if (letters.stream().noneMatch(Character::isDigit)) throw new WeakPasswordException("Password must contain at least 1 digit!");
        if (specialChars.stream().noneMatch(c -> password.contains(c.toString()))) throw new WeakPasswordException("Password must contain at least 1 special character '@', '#', '$', '_', '/'!");
        if (password.length() < 8) throw new WeakPasswordException("Password must be 8 character long!");
    }
}
