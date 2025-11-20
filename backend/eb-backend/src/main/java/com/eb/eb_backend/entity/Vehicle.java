package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "license_plate", unique = true, nullable = false)
    private String licensePlate;
    
    @Column(name = "brand", nullable = false)
    private String brand;
    
    @Column(name = "model", nullable = false)
    private String model;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "battery_capacity")
    private Double batteryCapacity; // en kWh
    
    @ManyToMany
    @JoinTable(
        name = "vehicle_plug_compatibility",
        joinColumns = @JoinColumn(name = "vehicle_id"),
        inverseJoinColumns = @JoinColumn(name = "plug_type_id")
    )
    private Set<PlugType> compatiblePlugs = new HashSet<>();
    
    @ManyToMany(mappedBy = "vehicles")
    private Set<User> users = new HashSet<>();
    
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
    public Vehicle() {}
    
    public Vehicle(String licensePlate, String brand, String model) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public void setYear(Integer year) {
        this.year = year;
    }
    
    public Double getBatteryCapacity() {
        return batteryCapacity;
    }
    
    public void setBatteryCapacity(Double batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }
    
    public Set<PlugType> getCompatiblePlugs() {
        return compatiblePlugs;
    }
    
    public void setCompatiblePlugs(Set<PlugType> compatiblePlugs) {
        this.compatiblePlugs = compatiblePlugs;
    }
    
    public Set<User> getUsers() {
        return users;
    }
    
    public void setUsers(Set<User> users) {
        this.users = users;
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
    
    public String getDisplayName() {
        return brand + " " + model + " (" + licensePlate + ")";
    }
}





























