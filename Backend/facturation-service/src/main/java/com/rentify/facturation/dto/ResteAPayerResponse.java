package com.rentify.facturation.dto;

/** Situation de paiement d'une facture. */
public record ResteAPayerResponse(Long factureId, double montantTotal, double totalPaye, double reste) {
}
