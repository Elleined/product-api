package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.utils.StringUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void validate() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "@Qwerty1";

        assertDoesNotThrow(() -> passwordValidator.validate(password));
    }

    @Test
    void shouldNotContainWhiteSpaces() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "password";

        assertFalse(passwordValidator.containsWhiteSpace(password));
    }

    @Test
    void shouldContainAtLeastOneUpperCase() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "Password";
        List<Character> letters = StringUtil.toCharArray(password);

        assertFalse(passwordValidator.containsAtLeastOneUpperCase(letters));
    }

    @Test
    void shouldContainAtLeastOneLowerCase() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "PaSSWORD";
        List<Character> letters = StringUtil.toCharArray(password);

        assertFalse(passwordValidator.containsAtLeastOneLowerCase(letters));
    }

    @Test
    void shouldContainAtLeastOneDigit() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "password1";
        List<Character> letters = StringUtil.toCharArray(password);

        assertFalse(passwordValidator.containsAtLeastOneDigit(letters));
    }

    @Test
    void shouldContainAtLeastOneSpecialCharacter() {
        // List<Character> specialChars = Arrays.asList('@', '#', '$', '_', '/');
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "@Password";
        assertFalse(passwordValidator.containsAtLeastOneSpecialChar(password));
    }

    @Test
    void shouldBeNLettersLong() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "password";
        int preferredPasswordLength = 8;

        assertEquals(preferredPasswordLength, password.length());
        assertFalse(passwordValidator.isNLettersLong(password, preferredPasswordLength));
    }

    @Test
    void isPasswordNotMatch() {
        // Mock data
        PasswordValidator passwordValidator = new PasswordValidator();
        String password = "password";
        String confirmPassword = "confirmPassword";

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertTrue(passwordValidator.isPasswordNotMatch(password, confirmPassword));

        // Behavior verification
    }
}