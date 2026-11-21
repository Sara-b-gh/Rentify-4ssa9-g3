package com.rentify.utilisateurs.repository;

import com.rentify.utilisateurs.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<Utilisateur> findByKeycloakId(String keycloakId);
}
