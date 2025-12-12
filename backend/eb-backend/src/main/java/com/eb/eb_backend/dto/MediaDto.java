package com.eb.eb_backend.dto;

import com.eb.eb_backend.entity.Media;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    
    private Long id;
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 255)
    private String name;
    
    @NotBlank(message = "L'URL est obligatoire")
    private String url;
    
    @NotNull(message = "Le type est obligatoire")
    private String type; // IMAGE ou VIDEO
    
    @Size(max = 1000)
    private String description;
    
    private Long fileSize;
    private String mimeType;
    
    private Long stationId;
    
    // Constructeur pour conversion depuis l'entité
    public MediaDto(Media media) {
        this.id = media.getId();
        this.name = media.getName();
        this.url = media.getUrl();
        this.type = media.getType() != null ? media.getType().name() : null;
        this.description = media.getDescription();
        this.fileSize = media.getFileSize();
        this.mimeType = media.getMimeType();
        
        if (media.getStation() != null) {
            this.stationId = media.getStation().getId();
        }
    }
}































