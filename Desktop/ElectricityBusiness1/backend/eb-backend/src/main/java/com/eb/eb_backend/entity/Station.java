package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "stations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"owner_id", "name"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Le propriétaire est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @NotNull(message = "Le lieu est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255, message = "Le nom ne peut pas dépasser 255 caractères")
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @NotNull(message = "Le tarif horaire est obligatoire")
    @DecimalMin(value = "0.0", message = "Le tarif horaire doit être positif")
    @Digits(integer = 8, fraction = 2, message = "Format de tarif invalide")
    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate = BigDecimal.ZERO;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StationStatus status = StationStatus.ACTIVE;
    
    @Column(name = "power", precision = 10, scale = 2)
    private BigDecimal power;
    
    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;
    
    @Column(name = "on_foot")
    private Boolean onFoot = false;

    // Coordonnées spécifiques à la borne (optionnelles). Si null, hérite de Location
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relations
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "station_plug_type",
        joinColumns = @JoinColumn(name = "station_id"),
        inverseJoinColumns = @JoinColumn(name = "plug_type_id")
    )
    private Set<PlugType> plugTypes = new HashSet<>();
    
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> medias = new ArrayList<>();
}
