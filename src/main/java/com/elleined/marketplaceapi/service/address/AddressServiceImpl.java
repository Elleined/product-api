package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.dto.AddressDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.AddressMapper;
import com.elleined.marketplaceapi.model.address.Address;
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

    private static final int DELIVERY_ADDRESS_LIMIT = 5;

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public void saveUserAddress(User registeringUser, AddressDTO addressDTO) {
        UserAddress userAddress = addressMapper.toUserAddressEntity(addressDTO, registeringUser);
        registeringUser.setAddress(userAddress);
        addressRepository.save(userAddress);
        log.debug("User with id of {} successfully registered with address id of {}", registeringUser.getId(), userAddress.getId());
    }

    @Override
    public DeliveryAddress saveDeliveryAddress(User orderingUser, AddressDTO addressDTO) {
        DeliveryAddress deliveryAddress = addressMapper.toDeliveryAddressEntity(addressDTO, orderingUser);
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
    public boolean isUserHas5DeliveryAddress(User currentUser) {
        return getAllDeliveryAddress(currentUser).size() == DELIVERY_ADDRESS_LIMIT;
    }

    @Override
    public List<String> getAllAddressDetails() {
        return addressRepository.fetchAllAddressDetails();
    }

    @Override
    public DeliveryAddress getDeliveryAddressById(User user, int deliveryAddressId) throws ResourceNotFoundException {
        return user.getDeliveryAddresses().stream()
                .filter(deliveryAddress -> deliveryAddress.getId() == deliveryAddressId)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id of " + user.getId() + " does not have delivery address with id of " + deliveryAddressId));
    }

    @Override
    public void deleteDeliveryAddress(User currentUser, int deliveryAddressId) {
        addressRepository.deleteById(deliveryAddressId);
        log.debug("User with id of {} deleted delivery address with id of {}", currentUser.getId(), deliveryAddressId);
    }
}
