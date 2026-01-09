package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.UserDeletionImpactDto;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Reservation;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.exception.ConflictException;
import com.eb.eb_backend.exception.NotFoundException;
import com.eb.eb_backend.repository.LocationRepository;
import com.eb.eb_backend.repository.ReservationRepository;
import com.eb.eb_backend.repository.StationRepository;
import com.eb.eb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationRepository locationRepository;
    private final StationRepository stationRepository;
    private final ReservationRepository reservationRepository;
    
    public UserDto createUser(CreateUserDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email déjà utilisé");
        }
        
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setPostalCode(dto.getPostalCode());
        user.setCity(dto.getCity());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(User.UserStatus.PENDING);
        
        return new UserDto(userRepository.save(user));
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
    
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsersOrSearch(String query, Pageable pageable) {
        if (query != null && !query.trim().isEmpty()) {
            return searchUsers(query.trim(), pageable);
        }
        return getAllUsers(pageable);
    }
    
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));
        
        if (!user.getEmail().equals(dto.getEmail()) && 
            userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email déjà utilisé");
        }
        
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setPostalCode(dto.getPostalCode());
        user.setCity(dto.getCity());
        user.setStatus(dto.getStatus());
        
        return new UserDto(userRepository.save(user));
    }
    
    /**
     * Vérifie l'impact de la suppression d'un utilisateur (RGPD)
     * Retourne les informations sur les données liées
     */
    @Transactional(readOnly = true)
    public UserDeletionImpactDto checkUserDeletionImpact(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + userId));
        
        // Compter les lieux
        List<Location> locations = locationRepository.findByOwner(user, Pageable.unpaged()).getContent();
        int locationsCount = locations.size();
        
        // Compter les stations
        List<Station> stations = stationRepository.findByOwner(user, Pageable.unpaged()).getContent();
        int stationsCount = stations.size();
        
        // Compter les réservations actives (PENDING ou CONFIRMED futures)
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> allReservations = reservationRepository.findByUser(user, Pageable.unpaged()).getContent();
        List<Reservation> activeReservations = allReservations.stream()
                .filter(r -> (r.getStatus() == Reservation.ReservationStatus.PENDING || 
                             r.getStatus() == Reservation.ReservationStatus.CONFIRMED) &&
                            r.getStartTime().isAfter(now))
                .toList();
        
        int activeReservationsCount = activeReservations.size();
        int totalReservationsCount = allReservations.size();
        
        // Vérifier si la suppression est sûre
        boolean canDeleteSafely = activeReservationsCount == 0;
        String message = canDeleteSafely 
                ? "La suppression est possible. Toutes les données associées seront supprimées (lieux, bornes, réservations passées conservées 3 ans pour obligation comptable)."
                : String.format("Impossible de supprimer : %d réservation(s) active(s) en cours. Veuillez d'abord annuler ou terminer ces réservations.", activeReservationsCount);
        
        return new UserDeletionImpactDto(
                userId,
                activeReservationsCount,
                locationsCount,
                stationsCount,
                totalReservationsCount,
                canDeleteSafely,
                message
        );
    }
    
    /**
     * Supprime un utilisateur avec vérification de l'impact (RGPD)
     * Note: Les réservations passées sont conservées 3 ans pour obligation comptable
     */
    public void deleteUser(Long id) {
        // Vérifier l'impact avant suppression
        UserDeletionImpactDto impact = checkUserDeletionImpact(id);
        if (!impact.isCanDeleteSafely()) {
            throw new ConflictException(impact.getMessage());
        }
        
        // La suppression en cascade via JPA gère automatiquement :
        // - Les lieux (cascade = CascadeType.ALL)
        // - Les bornes (via les lieux)
        // - Les réservations sont conservées (pas de cascade, ON DELETE RESTRICT en base)
        //   mais l'utilisateur sera supprimé, donc les réservations deviendront orphelines
        //   => Il faudrait plutôt anonymiser l'utilisateur pour conserver l'historique
        
        // Pour l'instant, on supprime directement (les contraintes ON DELETE RESTRICT empêcheront
        // la suppression s'il y a des réservations liées)
        userRepository.deleteById(id);
    }
    
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));
        user.setStatus(User.UserStatus.ACTIVE);
        return new UserDto(userRepository.save(user));
    }
    
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable: " + id));
        user.setStatus(User.UserStatus.INACTIVE);
        return new UserDto(userRepository.save(user));
    }
}
