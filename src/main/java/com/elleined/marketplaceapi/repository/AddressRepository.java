package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}