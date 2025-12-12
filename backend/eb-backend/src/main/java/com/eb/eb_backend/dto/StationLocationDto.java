package com.eb.eb_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationLocationDto {
    
    private Long id;
    private String name;
    private BigDecimal hourlyRate;
    private String plugType;
    private Boolean isActive;
    
    // Coordonnées de la station
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String address;
    
    // Informations sur le lieu
    private String locationLabel;
    private String locationDescription;
    
    // Informations sur le propriétaire
    private Long ownerId;
    private String ownerName;
    
    // Constructeur depuis l'entité Station
    public StationLocationDto(com.eb.eb_backend.entity.Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.hourlyRate = station.getHourlyRate();
        this.plugType = station.getPlugType();
        this.isActive = station.getStatus() == com.eb.eb_backend.entity.StationStatus.ACTIVE;
        
        // Coordonnées depuis le lieu (location)
        if (station.getLocation() != null) {
            this.latitude = station.getLocation().getLatitude();
            this.longitude = station.getLocation().getLongitude();
            this.locationLabel = station.getLocation().getLabel();
            this.locationDescription = station.getLocation().getDescription();
            
            // Adresse construite depuis les coordonnées GPS
            this.address = (this.latitude != null && this.longitude != null)
                    ? "Lat: " + this.latitude + ", Long: " + this.longitude
                    : "Adresse non disponible";
        } else {
            // Pas de location associée
            this.latitude = null;
            this.longitude = null;
            this.locationLabel = "Non définie";
            this.locationDescription = "";
            this.address = "Adresse non disponible";
        }
        
        // Propriétaire (accessible via location.owner)
        if (station.getLocation() != null && station.getLocation().getOwner() != null) {
            this.ownerId = station.getLocation().getOwner().getId();
            this.ownerName = station.getLocation().getOwner().getFullName();
        } else {
            this.ownerId = null;
            this.ownerName = "Propriétaire inconnu";
        }
    }
    
    // Méthodes utilitaires
    public String getFormattedAddress() {
        return address + " (" + locationLabel + ")";
    }
    
    public String getFormattedRate() {
        return hourlyRate + " €/h";
    }
}
































