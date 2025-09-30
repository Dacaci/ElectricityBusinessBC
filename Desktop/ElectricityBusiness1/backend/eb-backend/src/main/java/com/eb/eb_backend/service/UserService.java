package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserDto createUser(CreateUserDto createUserDto) {
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        // Créer l'utilisateur
        User user = new User();
        user.setFirstName(createUserDto.getFirstName());
        user.setLastName(createUserDto.getLastName());
        user.setEmail(createUserDto.getEmail());
        user.setPhone(createUserDto.getPhone());
        user.setDateOfBirth(createUserDto.getDateOfBirth());
        user.setAddress(createUserDto.getAddress());
        user.setPostalCode(createUserDto.getPostalCode());
        user.setCity(createUserDto.getCity());
        user.setPasswordHash(passwordEncoder.encode(createUserDto.getPassword()));
        user.setStatus(User.UserStatus.PENDING);
        
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDto::new);
    }
    
    @Transactional(readOnly = true)
    public Optional<UserDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto::new);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String query, Pageable pageable) {
        return userRepository.findBySearchQuery(query, pageable)
                .map(UserDto::new);
    }
    
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
        
        // Vérifier si l'email est changé et s'il existe déjà
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPhone(userDto.getPhone());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setAddress(userDto.getAddress());
        user.setPostalCode(userDto.getPostalCode());
        user.setCity(userDto.getCity());
        user.setStatus(userDto.getStatus());
        
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
        
        user.setStatus(User.UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
    
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID: " + id));
        
        user.setStatus(User.UserStatus.INACTIVE);
        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }
}
