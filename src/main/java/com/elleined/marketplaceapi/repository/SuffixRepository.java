package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.user.Suffix;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuffixRepository extends JpaRepository<Suffix, Integer> {
}