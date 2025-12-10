package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Address;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    
    private Long id;
    
    private String name;
    
    @NotBlank(message = "La rue est obligatoire")
    @Size(max = 255)
    private String street;
    
    @NotBlank(message = "Le code postal est obligatoire")
    @Size(max = 10)
    private String postalCode;
    
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100)
    private String city;
    
    @NotBlank(message = "Le pays est obligatoire")
    @Size(max = 100)
    private String country;
    
    @Size(max = 100)
    private String region;
    
    @Size(max = 500)
    private String complement;
    
    @Size(max = 20)
    private String floor;
    
    private Double latitude;
    private Double longitude;
    
    private Long userId;
    
    // Constructeur pour conversion depuis l'entité
    public AddressDto(Address address) {
        this.id = address.getId();
        this.name = address.getName();
        this.street = address.getStreet();
        this.postalCode = address.getPostalCode();
        this.city = address.getCity();
        this.country = address.getCountry();
        this.region = address.getRegion();
        this.complement = address.getComplement();
        this.floor = address.getFloor();
        this.latitude = address.getLatitude();
        this.longitude = address.getLongitude();
        if (address.getUser() != null) {
            this.userId = address.getUser().getId();
        }
    }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (street != null) sb.append(street);
        if (postalCode != null) sb.append(", ").append(postalCode);
        if (city != null) sb.append(" ").append(city);
        if (country != null) sb.append(", ").append(country);
        return sb.toString();
    }
}































