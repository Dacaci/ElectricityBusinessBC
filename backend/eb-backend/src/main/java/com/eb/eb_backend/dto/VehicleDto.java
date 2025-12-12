package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Vehicle;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDto {
    
    private Long id;
    
    @NotBlank(message = "La plaque d'immatriculation est obligatoire")
    @Size(max = 20)
    private String licensePlate;
    
    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 100)
    private String brand;
    
    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100)
    private String model;
    
    @Min(value = 1900, message = "L'année doit être supérieure à 1900")
    @Max(value = 2100, message = "L'année doit être inférieure à 2100")
    private Integer year;
    
    @DecimalMin(value = "0.0", message = "La capacité de batterie doit être positive")
    private Double batteryCapacity;
    
    private String plugType = "TYPE_2S";
    
    // Constructeur pour conversion depuis l'entité
    public VehicleDto(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.licensePlate = vehicle.getLicensePlate();
        this.brand = vehicle.getBrand();
        this.model = vehicle.getModel();
        this.year = vehicle.getYear();
        this.batteryCapacity = vehicle.getBatteryCapacity();
        this.plugType = vehicle.getPlugType();
    }
    
    public String getDisplayName() {
        return brand + " " + model + " (" + licensePlate + ")";
    }
}































