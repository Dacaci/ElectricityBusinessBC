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
        this.isActive = station.getIsActive();
        
        // Coordonnées depuis la location
        this.latitude = station.getLocation().getLatitude();
        this.longitude = station.getLocation().getLongitude();
        this.address = station.getLocation().getAddress();
        this.locationLabel = station.getLocation().getLabel();
        this.locationDescription = station.getLocation().getDescription();
        
        // Propriétaire
        this.ownerId = station.getOwner().getId();
        this.ownerName = station.getOwner().getFullName();
    }
    
    // Méthodes utilitaires
    public String getFormattedAddress() {
        return address + " (" + locationLabel + ")";
    }
    
    public String getFormattedRate() {
        return hourlyRate + " €/h";
    }
}








