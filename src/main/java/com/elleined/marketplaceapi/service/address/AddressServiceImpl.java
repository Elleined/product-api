package com.elleined.marketplaceapi.service.address;

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
public class AddressServiceImpl {

    private static final int DELIVERY_ADDRESS_LIMIT = 5;

    private final AddressRepository addressRepository;

    void saveUserAddress(UserAddress userAddress) {
        addressRepository.save(userAddress);
        log.debug("UserAddress with id of {} saved successfully!", userAddress.getId());
    }

    void saveDeliveryAddress(DeliveryAddress deliveryAddress) {
        addressRepository.save(deliveryAddress);
        log.debug("Delivery address with id of {} saved successfully!", deliveryAddress.getId());
    }

    UserAddress getUserAddress(User currentUser) {
        return currentUser.getAddress();
    }

    List<DeliveryAddress> getAllDeliveryAddress(User currentUser) {
        return currentUser.getDeliveryAddresses();
    }

    boolean isUserHas5DeliveryAddress(User currentUser) {
        return getAllDeliveryAddress(currentUser).size() == DELIVERY_ADDRESS_LIMIT;
    }
}
