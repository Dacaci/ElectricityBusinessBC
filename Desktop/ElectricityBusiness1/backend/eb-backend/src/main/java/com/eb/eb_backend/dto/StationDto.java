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
    
    @NotBlank(message = "Le type de prise est obligatoire")
    @Pattern(regexp = "^(TYPE2S)$", 
             message = "Seul le type TYPE2S est supporté")
    private String plugType;
    
    private Boolean isActive = true;
    
    private StationStatus status = StationStatus.ACTIVE;
    
    private BigDecimal power;
    
    private String city;
    
    private BigDecimal latitude;
    
    private BigDecimal longitude;
    
    private String instructions;
    
    private Boolean onFoot = false;
    
    private Long ownerId;
    private Long locationId;
    
    // Constructeur pour conversion depuis l'entité
    public StationDto(Station station) {
        this.id = station.getId();
        this.name = station.getName();
        this.hourlyRate = station.getHourlyRate();
        this.plugType = station.getPlugType();
        this.isActive = station.getIsActive();
        this.status = station.getStatus();
        this.power = station.getPower();
        this.city = station.getCity();
        this.latitude = station.getLatitude();
        this.longitude = station.getLongitude();
        this.instructions = station.getInstructions();
        this.onFoot = station.getOnFoot();
        this.ownerId = station.getOwner().getId();
        this.locationId = station.getLocation().getId();
    }
}
