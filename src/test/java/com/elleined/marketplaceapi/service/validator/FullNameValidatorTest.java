package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.FullNameException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class FullNameValidatorTest {

    @Test
    void validate() {
        FullNameValidator fullNameValidator = new FullNameValidator();
        assertDoesNotThrow(() -> fullNameValidator.validate(
                UserDTO.UserDetailsDTO.builder()
                        .firstName("Your name")
                        .middleName("Your middle name")
                        .lastName("Your last name")
                        .build()
        ));
    }

    @Test
    void shouldNotContainAnyLetters() {
        FullNameValidator fullNameValidator = new FullNameValidator();
        assertThrowsExactly(FullNameException.class, () -> fullNameValidator.validate(
                UserDTO.UserDetailsDTO.builder()
                        .firstName("Your n5me")
                        .middleName("Your m1ddle name")
                        .lastName("Your last nam3")
                        .build()
        ));
    }
}