package com.elleined.marketplaceapi.service.address;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;

import java.util.List;

public interface AddressService {
    void saveUserAddress(UserAddress userAddress);

    void saveDeliveryAddress(DeliveryAddress deliveryAddress);

    UserAddress getUserAddress(User currentUser);

    List<DeliveryAddress> getAllDeliveryAddress(User currentUser);

    boolean isUserHas5DeliveryAddress(User currentUser);
}
