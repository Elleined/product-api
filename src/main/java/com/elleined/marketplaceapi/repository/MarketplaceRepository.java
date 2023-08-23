package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.AppWallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketplaceRepository extends JpaRepository<AppWallet, Integer> {
}
