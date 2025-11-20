package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.PlugTypeDto;
import com.eb.eb_backend.entity.PlugType;
import com.eb.eb_backend.repository.PlugTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlugTypeService {
    
    @Autowired
    private PlugTypeRepository plugTypeRepository;
    
    @Transactional(readOnly = true)
    public List<PlugTypeDto> getAllPlugTypes() {
        return plugTypeRepository.findAll().stream()
            .map(PlugTypeDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PlugTypeDto getPlugTypeById(Long id) {
        PlugType plugType = plugTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Type de prise non trouvé avec l'ID: " + id));
        return new PlugTypeDto(plugType);
    }
    
    @Transactional(readOnly = true)
    public PlugTypeDto getPlugTypeByName(String name) {
        PlugType plugType = plugTypeRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException("Type de prise non trouvé: " + name));
        return new PlugTypeDto(plugType);
    }
    
    @Transactional
    public PlugTypeDto createPlugType(PlugTypeDto plugTypeDto) {
        // Vérifier que le nom n'existe pas déjà
        if (plugTypeRepository.findByName(plugTypeDto.getName()).isPresent()) {
            throw new IllegalArgumentException("Un type de prise avec ce nom existe déjà");
        }
        
        PlugType plugType = new PlugType();
        plugType.setName(plugTypeDto.getName());
        plugType.setDescription(plugTypeDto.getDescription());
        plugType.setMaxPower(plugTypeDto.getMaxPower());
        
        PlugType savedPlugType = plugTypeRepository.save(plugType);
        return new PlugTypeDto(savedPlugType);
    }
    
    @Transactional
    public PlugTypeDto updatePlugType(Long id, PlugTypeDto plugTypeDto) {
        PlugType plugType = plugTypeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Type de prise non trouvé avec l'ID: " + id));
        
        if (plugTypeDto.getName() != null) {
            plugType.setName(plugTypeDto.getName());
        }
        if (plugTypeDto.getDescription() != null) {
            plugType.setDescription(plugTypeDto.getDescription());
        }
        if (plugTypeDto.getMaxPower() != null) {
            plugType.setMaxPower(plugTypeDto.getMaxPower());
        }
        
        PlugType savedPlugType = plugTypeRepository.save(plugType);
        return new PlugTypeDto(savedPlugType);
    }
    
    @Transactional
    public void deletePlugType(Long id) {
        if (!plugTypeRepository.existsById(id)) {
            throw new IllegalArgumentException("Type de prise non trouvé avec l'ID: " + id);
        }
        plugTypeRepository.deleteById(id);
    }
}








