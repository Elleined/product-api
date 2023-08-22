package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.exception.AlreadExistException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {
    private final AddressService addressService;

    public void validateAddressDetails(AddressDTO addressDTO) throws AlreadExistException {
        if (addressService.getAllAddressDetails().contains(addressDTO.getDetails())) throw new AlreadExistException("Address details of " + addressDTO.getDetails() + " already been taken by another user");
    }
}
