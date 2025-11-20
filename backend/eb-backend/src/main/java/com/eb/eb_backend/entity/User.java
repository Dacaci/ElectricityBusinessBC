package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Size(max = 255, message = "L'email ne peut pas dépasser 255 caractères")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;
    
    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Size(max = 1000, message = "L'adresse ne peut pas dépasser 1000 caractères")
    @Column(name = "address", length = 1000)
    private String address;
    
    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    @Column(name = "postal_code", length = 10)
    private String postalCode;
    
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    @Column(name = "city", length = 100)
    private String city;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relations
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Location> locations = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "user_vehicle",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "vehicle_id")
    )
    private List<Vehicle> vehicles = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> medias = new ArrayList<>();
    
    // Méthodes utilitaires
    public String getFullName() {
        return (firstName != null && lastName != null) ? firstName + " " + lastName : email;
    }
    
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
    
    // Enum pour le statut
    public enum UserStatus {
        PENDING, ACTIVE, INACTIVE, SUSPENDED
    }
}
