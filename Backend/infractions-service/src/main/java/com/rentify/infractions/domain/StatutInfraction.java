package com.rentify.infractions.domain;

/** Cycle de vie d'une infraction. */
public enum StatutInfraction {
    /** L'infraction vient d'être enregistrée ; le conducteur n'est pas encore connu. */
    DECLAREE,
    /** Le contrat actif et le conducteur ont été trouvés. */
    CONDUCTEUR_IDENTIFIE,
    /** Aucun contrat n'était actif à cette date : l'amende reste à la charge de l'agence. */
    NON_IMPUTABLE
}
