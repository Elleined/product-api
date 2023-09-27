package com.elleined.marketplaceapi.service.validator;

import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NumberValidator implements Validator<String> {
    @Override
    public void validate(String number) throws MobileNumberException {
        List<Character> letters = StringUtil.toCharArray(number);

        if (letters.stream().anyMatch(Character::isLetter)) throw new MobileNumberException("Phone number cannot contain letters!");
        if (!number.startsWith("09")) throw new MobileNumberException("Phone number must starts with 09!");
        if (number.length() != 11) throw new MobileNumberException("Phone number must be 11 digits long!");
    }
}
