package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Location;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    
    private Long id;
    
    @NotBlank(message = "Le libellé est obligatoire")
    @Size(max = 255, message = "Le libellé ne peut pas dépasser 255 caractères")
    private String label;
    
    @NotBlank(message = "L'adresse est obligatoire")
    private String address;
    
    @NotNull(message = "La latitude est obligatoire")
    @DecimalMin(value = "-90.0", message = "La latitude doit être entre -90 et 90")
    @DecimalMax(value = "90.0", message = "La latitude doit être entre -90 et 90")
    private BigDecimal latitude;
    
    @NotNull(message = "La longitude est obligatoire")
    @DecimalMin(value = "-180.0", message = "La longitude doit être entre -180 et 180")
    @DecimalMax(value = "180.0", message = "La longitude doit être entre -180 et 180")
    private BigDecimal longitude;
    
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;
    
    private Boolean isActive = true;
    
    private Long ownerId;
    
    // Constructeur pour conversion depuis l'entité
    public LocationDto(Location location) {
        this.id = location.getId();
        this.label = location.getLabel();
        // Adresse construite depuis les coordonnées GPS
        this.address = "Lat: " + location.getLatitude() + ", Long: " + location.getLongitude();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.description = location.getDescription();
        this.isActive = location.getIsActive();
        this.ownerId = location.getOwner().getId();
    }
}
