package com.rentify.utilisateurs.domain;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "permis")
public class PermisConduire {
    @Id
    @GeneratedValue
    private UUID id;

    private String numero;
    private Date dateObtention;
    private Date dateExpiration;

    public boolean isValid() {
        return dateExpiration != null && new Date().before(dateExpiration);
    }
}
