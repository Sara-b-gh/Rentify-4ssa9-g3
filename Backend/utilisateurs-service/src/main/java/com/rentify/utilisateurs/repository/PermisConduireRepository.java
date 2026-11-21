package com.rentify.utilisateurs.repository;

import com.rentify.utilisateurs.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface PermisConduireRepository extends JpaRepository<PermisConduire, Long> {
    boolean existsByUtilisateurId(Long utilisateurId);
    Optional<PermisConduire> findByUtilisateurId(Long utilisateurId);
}
