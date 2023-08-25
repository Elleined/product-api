package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.user.Premium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumRepository extends JpaRepository<Premium, Integer> {
}