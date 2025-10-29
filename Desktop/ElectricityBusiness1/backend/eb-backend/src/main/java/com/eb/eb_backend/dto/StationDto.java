package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.StationStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationDto {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    private String name;
    
    @NotNull(message = "Le tarif horaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le tarif horaire doit être positif")
    private BigDecimal hourlyRate;
    
    private Boolean isActive = true;
    
    private StationStatus status = StationStatus.ACTIVE;
    
    private BigDecimal power;
    
    private String instructions;
    
    private Boolean onFoot = false;
    
    private Long ownerId;
    private Long locationId;
    
    // Constructeur pour conversion depuis l'entité
    public StationDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.hourlyRate = station.getHourlyRate();
        // plugType, city, latitude, longitude sont maintenant dans location/plugTypes
        this.isActive = station.getIsActive();
        this.status = station.getStatus();
        this.power = station.getPower();
        this.instructions = station.getInstructions();
        this.onFoot = station.getOnFoot();
        this.ownerId = station.getOwner().getId();
        this.locationId = station.getLocation().getId();
    }
}
