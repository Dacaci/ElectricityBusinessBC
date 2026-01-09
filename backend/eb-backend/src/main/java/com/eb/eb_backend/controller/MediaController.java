package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.MediaDto;
import com.eb.eb_backend.entity.Media.MediaType;
import com.eb.eb_backend.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/medias")
@CrossOrigin(origins = "*")
public class MediaController {
    
    @Autowired
    private MediaService mediaService;
    
    @GetMapping
    public ResponseEntity<List<MediaDto>> getAllMedias() {
        return ResponseEntity.ok(mediaService.getAllMedias());
    }
    
    @GetMapping("/station/{stationId}")
    public ResponseEntity<List<MediaDto>> getMediasByStationId(@PathVariable Long stationId) {
        return ResponseEntity.ok(mediaService.getMediasByStationId(stationId));
    }
    
    /**
     * Upload d'un média (photo ou vidéo) pour une station
     * @param stationId ID de la station
     * @param file Fichier à uploader
     * @param type Type de média (IMAGE ou VIDEO)
     * @return MediaDto créé
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadMedia(
            @RequestParam("stationId") Long stationId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") MediaType type,
            @RequestParam(value = "name", required = false) String customName,
            @RequestParam(value = "description", required = false) String description) {
        try {
            MediaDto created = mediaService.uploadMedia(stationId, file, type, customName, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'upload : " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(mediaService.getMediaById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<MediaDto> createMedia(@Valid @RequestBody MediaDto mediaDto) {
        try {
            MediaDto created = mediaService.createMedia(mediaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<MediaDto> updateMedia(
            @PathVariable Long id,
            @Valid @RequestBody MediaDto mediaDto) {
        try {
            return ResponseEntity.ok(mediaService.updateMedia(id, mediaDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Servir un fichier média uploadé par son nom de fichier
     * Endpoint pour servir les fichiers depuis /uploads/medias/
     */
    @GetMapping("/file/{filename:.+}")
    public ResponseEntity<?> getMediaFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get("uploads/medias", filename);
            if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
            
            return ResponseEntity.ok()
                    .header("Content-Type", contentType)
                    .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        try {
            mediaService.deleteMedia(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}







