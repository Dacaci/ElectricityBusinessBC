package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "plug_types")
public class PlugType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "max_power")
    private Double maxPower; // en kW
    
    @ManyToMany(mappedBy = "compatiblePlugs")
    private Set<Vehicle> compatibleVehicles = new HashSet<>();
    
    @ManyToMany(mappedBy = "plugTypes")
    private Set<Station> stations = new HashSet<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public PlugType() {}
    
    public PlugType(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getMaxPower() {
        return maxPower;
    }
    
    public void setMaxPower(Double maxPower) {
        this.maxPower = maxPower;
    }
    
    public Set<Vehicle> getCompatibleVehicles() {
        return compatibleVehicles;
    }
    
    public void setCompatibleVehicles(Set<Vehicle> compatibleVehicles) {
        this.compatibleVehicles = compatibleVehicles;
    }
    
    public Set<Station> getStations() {
        return stations;
    }
    
    public void setStations(Set<Station> stations) {
        this.stations = stations;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
































