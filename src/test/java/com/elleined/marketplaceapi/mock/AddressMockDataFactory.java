package com.elleined.marketplaceapi.mock;

import com.elleined.marketplaceapi.model.address.DeliveryAddress;

public interface AddressMockDataFactory {

    static DeliveryAddress getDeliveryAddress(int deliveryAddressId) {
        return DeliveryAddress.deliveryAddressBuilder()
                .id(deliveryAddressId)
                .regionName("Region name")
                .provinceName("Province name")
                .cityName("City name")
                .baranggayName("Baranggay name")
                .build();
    }
}
