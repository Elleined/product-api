package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.HasDigitException;
import com.elleined.marketplaceapi.exception.MobileNumberException;
import com.elleined.marketplaceapi.utils.StringUtil;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDetailsValidator {

    public void validatePhoneNumber(UserDTO.UserDetailsDTO userDetailsDTO) {
        String mobileNumber = userDetailsDTO.getMobileNumber();
        List<Character> letters = StringUtil.toCharArray(mobileNumber);

        if (letters.stream().anyMatch(Character::isLetter)) throw new MobileNumberException("Phone number cannot contain letters!");
        if (!mobileNumber.startsWith("09")) throw new MobileNumberException("Phone number must starts with 09!");
        if (mobileNumber.length() != 11) throw new MobileNumberException("Phone number must be 11 digits long!");
    }

    public void validateFullName(UserDTO.UserDetailsDTO userDetailsDTO) throws HasDigitException {
        String firstName = userDetailsDTO.getFirstName();
        String middleName = userDetailsDTO.getMiddleName();
        String lastName = userDetailsDTO.getLastName();

        List<Character> firstNameLetters = StringUtil.toCharArray(firstName);
        List<Character> middleNameLetters = StringUtil.toCharArray(middleName);
        List<Character> lastNameLetters = StringUtil.toCharArray(lastName);

        if (firstNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("Field must not contain any digit!");
        if (middleNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("Field must not contain any digit!");
        if (lastNameLetters.stream().anyMatch(Character::isDigit)) throw new HasDigitException("Field must not contain any digit!");
    }
}
