package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.DeliveryAddressLimitException;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface AddressService {
    int DELIVERY_ADDRESS_LIMIT = 5;

    void saveUserAddress(User registeringUser, AddressDTO addressDTO);

    DeliveryAddress saveDeliveryAddress(User orderingUser, DeliveryAddressDTO deliveryAddressDTO)
            throws DeliveryAddressLimitException;

    List<DeliveryAddress> getAllDeliveryAddress(User currentUser);

    DeliveryAddress getDeliveryAddressById(User user, int deliveryAddressId)
            throws ResourceNotFoundException;
}
