package com.eb.eb_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDateTime;

@Entity
@Table(name = "medias")
public class Media {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "url", nullable = false)
    private String url;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private MediaType type;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "file_size")
    private Long fileSize; // en bytes
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
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
    
    
    // Enum for Media Type
    public enum MediaType {
        IMAGE,
        VIDEO
    }
    
    // Constructors
    public Media() {}
    
    public Media(String name, String url, MediaType type) {
        this.name = name;
        this.url = url;
        this.type = type;
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
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public MediaType getType() {
        return type;
    }
    
    public void setType(MediaType type) {
        this.type = type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    // Validation métier : Un média doit illustrer exactement une entité
    @AssertTrue(message = "Un média doit illustrer exactement une entité (Station, Location ou User)")
    public boolean hasExactlyOneParent() {
        int count = 0;
        if (station != null) count++;
        if (location != null) count++;
        if (user != null) count++;
        return count == 1;
    }
    
    // Méthode helper pour définir le parent (assure l'exclusivité)
    public void setParentEntity(Object parent) {
        // Réinitialiser tous les parents
        this.station = null;
        this.location = null;
        this.user = null;
        
        // Définir le bon parent
        if (parent instanceof Station) {
            this.station = (Station) parent;
        } else if (parent instanceof Location) {
            this.location = (Location) parent;
        } else if (parent instanceof User) {
            this.user = (User) parent;
        } else if (parent != null) {
            throw new IllegalArgumentException("Le parent doit être Station, Location ou User");
        }
    }
    
    public Station getStation() {
        return station;
    }
    
    public void setStation(Station station) {
        this.station = station;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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





























