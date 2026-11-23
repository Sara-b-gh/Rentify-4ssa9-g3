package com.rentify.facturation.repository;

import com.rentify.facturation.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.*;
import java.util.*;

public interface PenaliteRepository extends JpaRepository<Penalite, Long> {
    boolean existsByInfractionId(Long infractionId);
}
