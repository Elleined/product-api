package com.elleined.marketplaceapi.mock;

import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;

public interface AddressMockDataFactory {
    static AddressDTO getAddress() {
        return AddressDTO.builder()
                .id(1)
                .regionName("Region name")
                .provinceName("Province name")
                .cityName("City name")
                .baranggayName("Barangay name")
                .build();
    }

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
