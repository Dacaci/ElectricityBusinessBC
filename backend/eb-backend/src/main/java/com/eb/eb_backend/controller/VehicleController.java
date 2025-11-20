package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.VehicleDto;
import com.eb.eb_backend.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
    public ResponseEntity<List<VehicleDto>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VehicleDto>> getVehiclesByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(vehicleService.getVehiclesByUser(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicleById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(vehicleService.getVehicleById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createVehicle(
            @Valid @RequestBody VehicleDto vehicleDto,
            @RequestParam(required = false) Long userId) {
        try {
            VehicleDto created = vehicleService.createVehicle(vehicleDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleDto vehicleDto) {
        try {
            return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicleDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}







