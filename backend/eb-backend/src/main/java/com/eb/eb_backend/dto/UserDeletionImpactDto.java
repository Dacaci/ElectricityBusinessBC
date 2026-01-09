package com.eb.eb_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour représenter l'impact de la suppression d'un utilisateur
 * Utilisé pour informer l'utilisateur avant suppression définitive (RGPD)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionImpactDto {
    private Long userId;
    private int activeReservationsCount;
    private int locationsCount;
    private int stationsCount;
    private int totalReservationsCount;
    private boolean canDeleteSafely;
    private String message;
}

