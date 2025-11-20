package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.MediaDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Media;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.LocationRepository;
import com.eb.eb_backend.repository.MediaRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Autowired
    private StationRepository stationRepository;
    
    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<MediaDto> getAllMedias() {
        return mediaRepository.findAll().stream()
            .map(MediaDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MediaDto> getMediasByStationId(Long stationId) {
        return mediaRepository.findByStationId(stationId).stream()
            .map(MediaDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MediaDto> getMediasByLocationId(Long locationId) {
        return mediaRepository.findByLocationId(locationId).stream()
            .map(MediaDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<MediaDto> getMediasByUserId(Long userId) {
        return mediaRepository.findByUserId(userId).stream()
            .map(MediaDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public MediaDto getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Média non trouvé avec l'ID: " + id));
        return new MediaDto(media);
    }
    
    @Transactional
    public MediaDto createMedia(MediaDto mediaDto) {
        // Validation : exactement un parent doit être fourni
        int parentCount = 0;
        if (mediaDto.getStationId() != null) parentCount++;
        if (mediaDto.getLocationId() != null) parentCount++;
        if (mediaDto.getUserId() != null) parentCount++;
        
        if (parentCount != 1) {
            throw new IllegalArgumentException("Un média doit être lié à exactement une entité (Station, Location ou User)");
        }
        
        Media media = new Media();
        media.setName(mediaDto.getName());
        media.setUrl(mediaDto.getUrl());
        
        // Convertir le type String en MediaType enum
        if (mediaDto.getType() != null) {
            try {
                media.setType(Media.MediaType.valueOf(mediaDto.getType()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Type de média invalide: " + mediaDto.getType());
            }
        }
        
        media.setDescription(mediaDto.getDescription());
        media.setFileSize(mediaDto.getFileSize());
        media.setMimeType(mediaDto.getMimeType());
        
        // Utiliser setParentEntity pour assurer l'exclusivité
        if (mediaDto.getStationId() != null) {
            Station station = stationRepository.findById(mediaDto.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("Station non trouvée: " + mediaDto.getStationId()));
            media.setParentEntity(station);
        } else if (mediaDto.getLocationId() != null) {
            Location location = locationRepository.findById(mediaDto.getLocationId())
                .orElseThrow(() -> new IllegalArgumentException("Lieu non trouvé: " + mediaDto.getLocationId()));
            media.setParentEntity(location);
        } else if (mediaDto.getUserId() != null) {
            User user = userRepository.findById(mediaDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + mediaDto.getUserId()));
            media.setParentEntity(user);
        }
        
        Media savedMedia = mediaRepository.save(media);
        return new MediaDto(savedMedia);
    }
    
    @Transactional
    public MediaDto updateMedia(Long id, MediaDto mediaDto) {
        Media media = mediaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Média non trouvé avec l'ID: " + id));
        
        if (mediaDto.getName() != null) {
            media.setName(mediaDto.getName());
        }
        if (mediaDto.getUrl() != null) {
            media.setUrl(mediaDto.getUrl());
        }
        if (mediaDto.getType() != null) {
            try {
                media.setType(Media.MediaType.valueOf(mediaDto.getType()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Type de média invalide: " + mediaDto.getType());
            }
        }
        if (mediaDto.getDescription() != null) {
            media.setDescription(mediaDto.getDescription());
        }
        if (mediaDto.getFileSize() != null) {
            media.setFileSize(mediaDto.getFileSize());
        }
        if (mediaDto.getMimeType() != null) {
            media.setMimeType(mediaDto.getMimeType());
        }
        
        Media savedMedia = mediaRepository.save(media);
        return new MediaDto(savedMedia);
    }
    
    @Transactional
    public void deleteMedia(Long id) {
        if (!mediaRepository.existsById(id)) {
            throw new IllegalArgumentException("Média non trouvé avec l'ID: " + id);
        }
        mediaRepository.deleteById(id);
    }
}








