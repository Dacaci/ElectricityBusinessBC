package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.MediaDto;
import com.eb.eb_backend.entity.Media;
import com.eb.eb_backend.entity.Media.MediaType;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.repository.MediaRepository;
import com.eb.eb_backend.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MediaService {
    
    @Autowired
    private MediaRepository mediaRepository;
    
    @Autowired
    private StationRepository stationRepository;
    
    @Value("${media.upload.dir:uploads/medias}")
    private String uploadDir;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/mpeg", "video/quicktime", "video/x-msvideo", "video/webm"
    );
    
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
    public MediaDto getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Média non trouvé avec l'ID: " + id));
        return new MediaDto(media);
    }
    
    /**
     * Upload d'un fichier média (photo ou vidéo) pour une station
     * @param customName Nom personnalisé (optionnel, utilise le nom du fichier par défaut)
     * @param description Description (optionnel)
     */
    @Transactional
    public MediaDto uploadMedia(Long stationId, MultipartFile file, MediaType type, String customName, String description) throws IOException {
        Station station = stationRepository.findById(stationId)
            .orElseThrow(() -> new IllegalArgumentException("Station non trouvée: " + stationId));
        
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Type de fichier non déterminé");
        }
        
        if (type == MediaType.IMAGE && !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Format d'image non supporté. Formats acceptés : JPG, PNG, GIF, WEBP");
        }
        
        if (type == MediaType.VIDEO && !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Format de vidéo non supporté. Formats acceptés : MP4, MPEG, MOV, AVI, WEBM");
        }
        
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        Media media = new Media();
        media.setName(customName != null && !customName.trim().isEmpty() ? customName.trim() : originalFilename);
        media.setUrl("/api/medias/file/" + uniqueFilename);
        media.setType(type);
        media.setMimeType(contentType);
        media.setFileSize(file.getSize());
        media.setStation(station);
        if (description != null && !description.trim().isEmpty()) {
            media.setDescription(description.trim());
        }
        
        return new MediaDto(mediaRepository.save(media));
    }
    
    @Transactional
    public MediaDto createMedia(MediaDto mediaDto) {
        if (mediaDto.getStationId() == null) {
            throw new IllegalArgumentException("Un média doit être lié à une station");
        }
        
        Station station = stationRepository.findById(mediaDto.getStationId())
            .orElseThrow(() -> new IllegalArgumentException("Station non trouvée: " + mediaDto.getStationId()));
        
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
        media.setStation(station);
        
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








