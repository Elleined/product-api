package com.elleined.marketplaceapi.mock;

import com.elleined.marketplaceapi.dto.address.AddressDTO;

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
}
