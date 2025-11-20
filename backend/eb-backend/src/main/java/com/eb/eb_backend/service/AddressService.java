package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.AddressDto;
import com.eb.eb_backend.entity.Address;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.AddressRepository;
import com.eb.eb_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    
    @Autowired
    private AddressRepository addressRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional(readOnly = true)
    public List<AddressDto> getAllAddresses() {
        return addressRepository.findAll().stream()
            .map(AddressDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AddressDto> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
            .map(AddressDto::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public AddressDto getAddressById(Long id) {
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Adresse non trouvée avec l'ID: " + id));
        return new AddressDto(address);
    }
    
    @Transactional
    public AddressDto createAddress(AddressDto addressDto, Long userId) {
        Address address = new Address();
        address.setName(addressDto.getName());
        address.setStreet(addressDto.getStreet());
        address.setPostalCode(addressDto.getPostalCode());
        address.setCity(addressDto.getCity());
        address.setCountry(addressDto.getCountry());
        address.setRegion(addressDto.getRegion());
        address.setComplement(addressDto.getComplement());
        address.setFloor(addressDto.getFloor());
        address.setLatitude(addressDto.getLatitude());
        address.setLongitude(addressDto.getLongitude());
        
        if (userId != null) {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé: " + userId));
            address.setUser(user);
        }
        
        Address savedAddress = addressRepository.save(address);
        return new AddressDto(savedAddress);
    }
    
    @Transactional
    public AddressDto updateAddress(Long id, AddressDto addressDto) {
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Adresse non trouvée avec l'ID: " + id));
        
        if (addressDto.getName() != null) {
            address.setName(addressDto.getName());
        }
        if (addressDto.getStreet() != null) {
            address.setStreet(addressDto.getStreet());
        }
        if (addressDto.getPostalCode() != null) {
            address.setPostalCode(addressDto.getPostalCode());
        }
        if (addressDto.getCity() != null) {
            address.setCity(addressDto.getCity());
        }
        if (addressDto.getCountry() != null) {
            address.setCountry(addressDto.getCountry());
        }
        if (addressDto.getRegion() != null) {
            address.setRegion(addressDto.getRegion());
        }
        if (addressDto.getComplement() != null) {
            address.setComplement(addressDto.getComplement());
        }
        if (addressDto.getFloor() != null) {
            address.setFloor(addressDto.getFloor());
        }
        if (addressDto.getLatitude() != null) {
            address.setLatitude(addressDto.getLatitude());
        }
        if (addressDto.getLongitude() != null) {
            address.setLongitude(addressDto.getLongitude());
        }
        
        Address savedAddress = addressRepository.save(address);
        return new AddressDto(savedAddress);
    }
    
    @Transactional
    public void deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new IllegalArgumentException("Adresse non trouvée avec l'ID: " + id);
        }
        addressRepository.deleteById(id);
    }
}








