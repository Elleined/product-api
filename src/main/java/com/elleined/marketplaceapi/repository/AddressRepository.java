package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    @Query("SELECT a.details FROM Address a")
    List<String> fetchAllAddressDetails();
}