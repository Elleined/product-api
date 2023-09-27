package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.HasDigitException;
import com.elleined.marketplaceapi.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FullNameValidator implements Validator<UserDTO.UserDetailsDTO>{
    @Override
    public void validate(UserDTO.UserDetailsDTO userDetailsDTO) throws HasDigitException {
        String firstName = userDetailsDTO.getFirstName();
        String middleName = userDetailsDTO.getMiddleName();
        String lastName = userDetailsDTO.getLastName();

        List<Character> firstNameLetters = StringUtil.toCharArray(firstName);
        List<Character> middleNameLetters = StringUtil.toCharArray(middleName);
        List<Character> lastNameLetters = StringUtil.toCharArray(lastName);

        if (firstNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("First name must not contain any digit!");
        if (middleNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("Middle name must not contain any digit!");
        if (lastNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("Last name must not contain any digit!");
    }
}
