package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.dto.address.DeliveryAddressDTO;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.DeliveryAddressLimitException;
import com.elleined.marketplaceapi.mapper.address.DeliveryAddressMapper;
import com.elleined.marketplaceapi.mapper.address.UserAddressMapper;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserAddressMapper userAddressMapper;
    private final DeliveryAddressMapper deliveryAddressMapper;

    @Override
    public void saveUserAddress(User registeringUser, AddressDTO addressDTO) {
        UserAddress userAddress = userAddressMapper.toEntity(addressDTO, registeringUser);
        registeringUser.setAddress(userAddress);
        addressRepository.save(userAddress);
        log.debug("User with id of {} successfully registered with address id of {}", registeringUser.getId(), userAddress.getId());
    }

    @Override
    public DeliveryAddress saveDeliveryAddress(User orderingUser, DeliveryAddressDTO deliveryAddressDTO) throws DeliveryAddressLimitException {
        if (orderingUser.hasReachedDeliveryAddressLimit()) throw new DeliveryAddressLimitException("Cannot save another delivery address! Because you already reached the limit which is 5");
        DeliveryAddress deliveryAddress = deliveryAddressMapper.toEntity(deliveryAddressDTO, orderingUser);
        orderingUser.getDeliveryAddresses().add(deliveryAddress);
        addressRepository.save(deliveryAddress);
        log.debug("User with id of {} successfully saved delivery address with id of {}", orderingUser.getId(), deliveryAddress.getId());
        return deliveryAddress;
    }

    @Override
    public List<DeliveryAddress> getAllDeliveryAddress(User currentUser) {
        return currentUser.getDeliveryAddresses();
    }

    @Override
    public DeliveryAddress getDeliveryAddressById(User user, int deliveryAddressId) throws ResourceNotFoundException {
        return user.getDeliveryAddresses().stream()
                .filter(deliveryAddress -> deliveryAddress.getId() == deliveryAddressId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cannot save another delivery address because you already reached the delivery address limit which is " + DELIVERY_ADDRESS_LIMIT));
    }
}
