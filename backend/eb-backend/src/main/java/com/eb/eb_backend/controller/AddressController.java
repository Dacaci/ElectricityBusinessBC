package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.AddressDto;
import com.eb.eb_backend.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
public class AddressController {
    
    @Autowired
    private AddressService addressService;
    
    @GetMapping
    public ResponseEntity<List<AddressDto>> getAllAddresses() {
        return ResponseEntity.ok(addressService.getAllAddresses());
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AddressDto>> getAddressesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AddressDto> getAddressById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(addressService.getAddressById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<AddressDto> createAddress(
            @Valid @RequestBody AddressDto addressDto,
            @RequestParam(required = false) Long userId) {
        try {
            AddressDto created = addressService.createAddress(addressDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AddressDto> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDto addressDto) {
        try {
            return ResponseEntity.ok(addressService.updateAddress(id, addressDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        try {
            addressService.deleteAddress(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}







