package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private Long id;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String lastName;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    private String email;
    
    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String phone;
    
    private LocalDate dateOfBirth;
    
    @Size(max = 1000, message = "L'adresse ne peut pas dépasser 1000 caractères")
    private String address;
    
    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String postalCode;
    
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String city;
    
    private User.UserStatus status;
    
    // Constructeur pour conversion depuis l'entité
    public UserDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.dateOfBirth = user.getDateOfBirth();
        this.address = user.getAddress();
        this.postalCode = user.getPostalCode();
        this.city = user.getCity();
        this.status = user.getStatus();
    }
}
