package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberValidatorTest {

    @Test
    void validate() {
        NumberValidator numberValidator = new NumberValidator();
        String number = "09999999999";
        assertDoesNotThrow(() -> numberValidator.validate(number));
    }

    @Test
    void mobileNumberShouldStartWith09() {
        NumberValidator numberValidator = new NumberValidator();
        String number = "99999999999";
        assertThrows(MobileNumberException.class, () -> numberValidator.validate(number));
    }

    @Test
    void mobileNumberShouldNotContainLetters() {
        NumberValidator numberValidator = new NumberValidator();
        String number = "9999a9a9999";
        assertThrows(MobileNumberException.class, () -> numberValidator.validate(number));
    }

    @Test
    void mobileNumberShouldBe11NumbersLong() {
        NumberValidator numberValidator = new NumberValidator();
        String number = "9999999999";
        assertThrows(MobileNumberException.class, () -> numberValidator.validate(number));
    }
}