package com.eb.eb_backend.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateReservationDto {
    
    @NotNull(message = "L'ID de la station est obligatoire")
    private Long stationId;
    
    private Long vehicleId;
    
    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    private LocalDateTime startTime;
    
    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime endTime;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    private String notes;
    
    // Validation métier
    @AssertTrue(message = "La date de fin doit être après la date de début")
    public boolean isValidDateRange() {
        return endTime != null && startTime != null && endTime.isAfter(startTime);
    }
    
    @AssertTrue(message = "La durée de réservation doit être d'au moins 1 heure")
    public boolean isValidDuration() {
        if (startTime == null || endTime == null) {
            return false;
        }
        return java.time.Duration.between(startTime, endTime).toHours() >= 1;
    }
}

