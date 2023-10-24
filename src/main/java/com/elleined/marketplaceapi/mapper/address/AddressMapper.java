package com.elleined.marketplaceapi.mapper.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.model.address.Address;
import com.elleined.marketplaceapi.model.user.User;

public interface AddressMapper<DTO extends AddressDTO, ENTITY extends Address> {
    ENTITY toEntity(DTO dto, User registeringUser);
    DTO toDTO(ENTITY entity);
}
