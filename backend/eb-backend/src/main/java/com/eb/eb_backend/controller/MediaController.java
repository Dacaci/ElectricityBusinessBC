package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.MediaDto;
import com.eb.eb_backend.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<MediaDto>> getMediasByLocationId(@PathVariable Long locationId) {
        return ResponseEntity.ok(mediaService.getMediasByLocationId(locationId));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MediaDto>> getMediasByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mediaService.getMediasByUserId(userId));
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







