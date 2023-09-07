package com.elleined.marketplaceapi.repository;

import com.elleined.marketplaceapi.model.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ModeratorRepository extends JpaRepository<Moderator, Integer> {

    @Query("SELECT m.moderatorCredential.email FROM Moderator m")
    List<String> fetchAllEmail();

    @Query("SELECT m FROM Moderator m WHERE m.moderatorCredential.email = :email")
    Optional<Moderator> fetchByEmail(@Param("email") String email);
}