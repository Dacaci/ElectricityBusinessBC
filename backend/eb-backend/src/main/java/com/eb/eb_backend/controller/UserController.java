package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.UserDeletionImpactDto;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.service.UserDataExportService;
import com.eb.eb_backend.service.UserService;
import com.eb.eb_backend.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final UserDataExportService userDataExportService;
    private final SecurityUtil securityUtil;
    
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto dto) {
        UserDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        Page<UserDto> users = userService.getAllUsersOrSearch(q, pageable);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto dto) {
        UserDto updated = userService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> activateUser(@PathVariable Long id) {
        UserDto userDto = userService.activateUser(id);
        return ResponseEntity.ok(userDto);
    }
    
    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable Long id) {
        UserDto userDto = userService.deactivateUser(id);
        return ResponseEntity.ok(userDto);
    }
    
    /**
     * Export complet des données utilisateur (RGPD - Droit à la portabilité)
     * Format JSON contenant : profil, lieux, bornes, réservations
     */
    @GetMapping(value = "/{id}/export", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> exportUserData(
            @PathVariable Long id,
            HttpServletRequest request) {
        // Vérifier que l'utilisateur demande ses propres données
        Long currentUserId = securityUtil.getCurrentUserId(request);
        if (currentUserId == null || !currentUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Map<String, Object> exportData = userDataExportService.exportUserData(id);
        return ResponseEntity.ok(exportData);
    }
    
    /**
     * Vérifie l'impact de la suppression d'un utilisateur (RGPD)
     * Permet à l'utilisateur de savoir ce qui sera supprimé avant de confirmer
     */
    @GetMapping("/{id}/deletion-impact")
    public ResponseEntity<UserDeletionImpactDto> checkDeletionImpact(
            @PathVariable Long id,
            HttpServletRequest request) {
        // Vérifier que l'utilisateur demande ses propres données
        Long currentUserId = securityUtil.getCurrentUserId(request);
        if (currentUserId == null || !currentUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        UserDeletionImpactDto impact = userService.checkUserDeletionImpact(id);
        return ResponseEntity.ok(impact);
    }
}
