package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.VehicleDto;
import com.eb.eb_backend.entity.PlugType;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.entity.Vehicle;
import com.eb.eb_backend.repository.PlugTypeRepository;
import com.eb.eb_backend.repository.UserRepository;
import com.eb.eb_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Autowired
    private PlugTypeRepository plugTypeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<VehicleDto> getAllVehicles() {
        return vehicleRepository.findAll().stream()
            .map(VehicleDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public VehicleDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + id));
        return new VehicleDto(vehicle);
    }
    
    @Transactional(readOnly = true)
    public List<VehicleDto> getVehiclesByUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + userId));
        
        return vehicleRepository.findByUsers(user).stream()
            .map(VehicleDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public VehicleDto createVehicle(VehicleDto vehicleDto, Long userId) {
        // Vérifier que la plaque n'existe pas déjà
        if (vehicleRepository.findByLicensePlate(vehicleDto.getLicensePlate()).isPresent()) {
            throw new IllegalArgumentException("Un véhicule avec cette plaque existe déjà");
        }
        
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setBrand(vehicleDto.getBrand());
        vehicle.setModel(vehicleDto.getModel());
        vehicle.setYear(vehicleDto.getYear());
        vehicle.setBatteryCapacity(vehicleDto.getBatteryCapacity());
        
        // Ajouter les types de prises compatibles
        if (vehicleDto.getCompatiblePlugIds() != null && !vehicleDto.getCompatiblePlugIds().isEmpty()) {
            Set<PlugType> plugTypes = new HashSet<>();
            for (Long plugId : vehicleDto.getCompatiblePlugIds()) {
                PlugType plugType = plugTypeRepository.findById(plugId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de prise non trouvé: " + plugId));
                plugTypes.add(plugType);
            }
            vehicle.setCompatiblePlugs(plugTypes);
        }
        
        // Associer l'utilisateur
        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));
            
            // Fix: Add vehicle to user (owning side of the relationship)
            // Because User owns the ManyToMany relationship, we must update User to save the link in user_vehicle table
            vehicle.getUsers().add(user);
            Vehicle savedVehicle = vehicleRepository.save(vehicle);
            
            user.getVehicles().add(savedVehicle);
            userRepository.save(user);
            
            return new VehicleDto(savedVehicle);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return new VehicleDto(savedVehicle);
    }
    
    @Transactional
    public VehicleDto updateVehicle(Long id, VehicleDto vehicleDto) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + id));
        
        if (vehicleDto.getBrand() != null) {
            vehicle.setBrand(vehicleDto.getBrand());
        }
        if (vehicleDto.getModel() != null) {
            vehicle.setModel(vehicleDto.getModel());
        }
        if (vehicleDto.getYear() != null) {
            vehicle.setYear(vehicleDto.getYear());
        }
        if (vehicleDto.getBatteryCapacity() != null) {
            vehicle.setBatteryCapacity(vehicleDto.getBatteryCapacity());
        }
        
        // Mettre à jour les types de prises compatibles
        if (vehicleDto.getCompatiblePlugIds() != null) {
            Set<PlugType> plugTypes = new HashSet<>();
            for (Long plugId : vehicleDto.getCompatiblePlugIds()) {
                PlugType plugType = plugTypeRepository.findById(plugId)
                    .orElseThrow(() -> new IllegalArgumentException("Type de prise non trouvé: " + plugId));
                plugTypes.add(plugType);
            }
            vehicle.setCompatiblePlugs(plugTypes);
        }
        
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return new VehicleDto(savedVehicle);
    }
    
    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new IllegalArgumentException("Véhicule non trouvé avec l'ID: " + id);
        }
        vehicleRepository.deleteById(id);
    }
}








