package com.eb.eb_backend.service;

// Removed JWT import
import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.LoginRequest;
import com.eb.eb_backend.dto.LoginResponse;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.UserRepository;
import com.eb.eb_backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
// Removed unused imports
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.isActive(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.isActive(), // accountNonLocked
                new ArrayList<>() // authorities
        );
    }
    
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }
        UserDto userDto = new UserDto(user);
        String token = jwtUtil.generateToken(user.getEmail(), java.util.Map.of("uid", user.getId()));
        return new LoginResponse(token, userDto);
    }
    
    public UserDto register(CreateUserDto createUserDto) {
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
    
    public boolean verifyEmail(String email, String code) {
        // Pour l'instant, on simule la vérification
        // Dans une vraie application, on vérifierait le code dans la base de données
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        if (user.getStatus() == User.UserStatus.PENDING) {
            user.setStatus(User.UserStatus.ACTIVE);
            userRepository.save(user);
            return true;
        }
        
        return false;
    }
}
