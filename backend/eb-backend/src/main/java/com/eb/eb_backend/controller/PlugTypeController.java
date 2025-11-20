package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.PlugTypeDto;
import com.eb.eb_backend.service.PlugTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plug-types")
@CrossOrigin(origins = "*")
public class PlugTypeController {
    
    @Autowired
    private PlugTypeService plugTypeService;
    
    @GetMapping
    public ResponseEntity<List<PlugTypeDto>> getAllPlugTypes() {
        return ResponseEntity.ok(plugTypeService.getAllPlugTypes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlugTypeDto> getPlugTypeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(plugTypeService.getPlugTypeById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<PlugTypeDto> getPlugTypeByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(plugTypeService.getPlugTypeByName(name));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<PlugTypeDto> createPlugType(@Valid @RequestBody PlugTypeDto plugTypeDto) {
        try {
            PlugTypeDto created = plugTypeService.createPlugType(plugTypeDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PlugTypeDto> updatePlugType(
            @PathVariable Long id,
            @Valid @RequestBody PlugTypeDto plugTypeDto) {
        try {
            return ResponseEntity.ok(plugTypeService.updatePlugType(id, plugTypeDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlugType(@PathVariable Long id) {
        try {
            plugTypeService.deletePlugType(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}







