package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "L'utilisateur est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @NotNull(message = "La station est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;
    
    @NotNull(message = "La date de début est obligatoire")
    @FutureOrPresent(message = "La date de début ne peut pas être dans le passé")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @NotNull(message = "La date de fin est obligatoire")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", message = "Le montant doit être positif")
    @Digits(integer = 8, fraction = 2, message = "Format de montant invalide")
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status = ReservationStatus.PENDING;
    
    @Size(max = 1000, message = "Les notes ne peuvent pas dépasser 1000 caractères")
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
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
    
    // Méthodes utilitaires
    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }
    
    public boolean isConfirmed() {
        return status == ReservationStatus.CONFIRMED;
    }
    
    public boolean isCancelled() {
        return status == ReservationStatus.CANCELLED;
    }
    
    public boolean isCompleted() {
        return status == ReservationStatus.COMPLETED;
    }
    
    // Calcul de la durée en heures
    public long getDurationInHours() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toHours();
    }
    
    // Méthodes métier pour les changements de statut
    public void confirm() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être confirmée");
        }
        this.status = ReservationStatus.CONFIRMED;
    }
    
    public void refuse() {
        if (status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Seule une réservation en attente peut être refusée");
        }
        this.status = ReservationStatus.REFUSED;
    }
    
    public void complete() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Seule une réservation confirmée peut être terminée");
        }
        if (endTime != null && endTime.isAfter(java.time.LocalDateTime.now())) {
            throw new IllegalStateException("La date de fin n'est pas encore atteinte");
        }
        this.status = ReservationStatus.COMPLETED;
    }
    
    public void cancel() {
        if (status == ReservationStatus.COMPLETED) {
            throw new IllegalStateException("Impossible d'annuler une réservation terminée");
        }
        this.status = ReservationStatus.CANCELLED;
    }
    
    // Enum pour le statut des réservations
    public enum ReservationStatus {
        PENDING,    // En attente de confirmation du propriétaire
        CONFIRMED,  // Confirmée par le propriétaire
        CANCELLED,  // Annulée
        COMPLETED,  // Terminée
        REFUSED     // Refusée par le propriétaire
    }
}

