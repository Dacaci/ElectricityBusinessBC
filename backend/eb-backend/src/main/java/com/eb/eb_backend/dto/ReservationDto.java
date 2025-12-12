package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Reservation;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {
    
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    
    private Long stationId;
    private String stationName;
    private Long locationId;
    private String locationLabel;
    private String locationAddress;
    private BigDecimal stationHourlyRate;
    
    private Long vehicleId;
    private String vehicleLicensePlate;
    private String vehicleBrandModel;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private Reservation.ReservationStatus status;
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructeur depuis l'entité
    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.userId = reservation.getUser().getId();
        this.userFirstName = reservation.getUser().getFirstName();
        this.userLastName = reservation.getUser().getLastName();
        this.userEmail = reservation.getUser().getEmail();
        
        this.stationId = reservation.getStation().getId();
        this.stationName = reservation.getStation().getName();
        this.locationId = reservation.getStation().getLocation() != null ? reservation.getStation().getLocation().getId() : null;
        this.locationLabel = reservation.getStation().getLocation() != null ? reservation.getStation().getLocation().getLabel() : "N/A";
        // Adresse construite depuis les coordonnées GPS du lieu
        this.locationAddress = reservation.getStation().getLocation() != null 
                ? "Lat: " + reservation.getStation().getLocation().getLatitude() + 
                  ", Long: " + reservation.getStation().getLocation().getLongitude()
                : "N/A";
        this.stationHourlyRate = reservation.getStation().getHourlyRate();
        
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
        this.totalAmount = reservation.getTotalAmount();
        this.status = reservation.getStatus();
        this.notes = reservation.getNotes();
        
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
    }
    
    // Méthodes utilitaires
    public String getUserFullName() {
        return userFirstName + " " + userLastName;
    }
    
    public long getDurationInHours() {
        if (startTime == null || endTime == null) {
            return 0;
        }
        return java.time.Duration.between(startTime, endTime).toHours();
    }
    
    public boolean isPending() {
        return status == Reservation.ReservationStatus.PENDING;
    }
    
    public boolean isConfirmed() {
        return status == Reservation.ReservationStatus.CONFIRMED;
    }
    
    public boolean isCancelled() {
        return status == Reservation.ReservationStatus.CANCELLED;
    }
    
    public boolean isCompleted() {
        return status == Reservation.ReservationStatus.COMPLETED;
    }
}

