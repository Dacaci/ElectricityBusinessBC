package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.PlugType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlugTypeDto {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String name;
    
    @Size(max = 500)
    private String description;
    
    @DecimalMin(value = "0.0", message = "La puissance maximale doit être positive")
    private Double maxPower;
    
    // Constructeur pour conversion depuis l'entité
    public PlugTypeDto(PlugType plugType) {
        this.id = plugType.getId();
        this.name = plugType.getName();
        this.description = plugType.getDescription();
        this.maxPower = plugType.getMaxPower();
    }
}




























