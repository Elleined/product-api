package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidatorTest {

    @Test
    void validate() {
        EmailValidator emailValidator = new EmailValidator();
        String email = "sampleEmail@gmail.com";

        assertDoesNotThrow(() -> emailValidator.validate(email));
    }

    @Test
    void shouldEndWithGmailDotCom() {
        EmailValidator emailValidator = new EmailValidator();
        String email = "sampleEmail@email.com";

        assertThrowsExactly(MalformedEmailException.class, () -> emailValidator.validate(email));
    }

    @Test
    void shouldNotStartsWithIterateA() {
        EmailValidator emailValidator = new EmailValidator();
        String email = "@gmail.com";

        assertThrowsExactly(MalformedEmailException.class, () -> emailValidator.validate(email));
    }
}