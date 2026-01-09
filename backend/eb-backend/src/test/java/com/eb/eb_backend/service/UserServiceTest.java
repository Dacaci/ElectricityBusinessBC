package com.eb.eb_backend.service;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.eb.eb_backend.repository.LocationRepository locationRepository;

    @Mock
    private com.eb.eb_backend.repository.StationRepository stationRepository;

    @Mock
    private com.eb.eb_backend.repository.ReservationRepository reservationRepository;

    @InjectMocks
    private UserService userService;

    private CreateUserDto createUserDto;
    private User user;
    private User savedUser;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createUserDto.setEmail("john.doe@example.com");
        createUserDto.setPassword("password123");
        createUserDto.setPhone("0123456789");
        createUserDto.setDateOfBirth(LocalDate.of(1990, 1, 15));
        createUserDto.setAddress("123 Test Street");
        createUserDto.setPostalCode("75001");
        createUserDto.setCity("Paris");

        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPasswordHash("hashedPassword");
        user.setStatus(User.UserStatus.PENDING);

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("John");
        savedUser.setLastName("Doe");
        savedUser.setEmail("john.doe@example.com");
        savedUser.setPasswordHash("hashedPassword");
        savedUser.setStatus(User.UserStatus.PENDING);
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe@example.com", result.getEmail());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(true);

        assertThrows(com.eb.eb_backend.exception.ConflictException.class, () -> {
            userService.createUser(createUserDto);
        });
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        Optional<UserDto> result = userService.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("john.doe@example.com", result.get().getEmail());
    }

    @Test
    void testGetUserById_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<UserDto> result = userService.getUserById(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetUserByEmail_Success() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(savedUser));

        Optional<UserDto> result = userService.getUserByEmail("john.doe@example.com");

        assertTrue(result.isPresent());
        assertEquals("john.doe@example.com", result.get().getEmail());
    }

    @Test
    void testGetAllUsers() {
        Page<User> page = new PageImpl<>(List.of(savedUser), PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDto> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchUsers() {
        Page<User> page = new PageImpl<>(List.of(savedUser), PageRequest.of(0, 10), 1);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findBySearchQuery(eq("john"), any(Pageable.class))).thenReturn(page);

        Page<UserDto> result = userService.searchUsers("john", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testUpdateUser_Success() {
        UserDto updateDto = new UserDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setEmail("jane.smith@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("jane.smith@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setFirstName("Jane");
            u.setLastName("Smith");
            u.setEmail("jane.smith@example.com");
            return u;
        });

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("jane.smith@example.com", result.getEmail());
    }

    @Test
    void testUpdateUser_NotFound() {
        UserDto updateDto = new UserDto();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.eb.eb_backend.exception.NotFoundException.class, () -> {
            userService.updateUser(999L, updateDto);
        });
    }

    @Test
    void testUpdateUser_EmailAlreadyExists() {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("existing@example.com");

        savedUser.setEmail("old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(com.eb.eb_backend.exception.ConflictException.class, () -> {
            userService.updateUser(1L, updateDto);
        });
    }

    @Test
    void testDeleteUser_Success() {
        savedUser.setStatus(User.UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        
        // Mock pour checkUserDeletionImpact : aucun lieu, station ou réservation active
        when(locationRepository.findByOwner(any(User.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(stationRepository.findByOwner(any(User.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        when(reservationRepository.findByUser(any(User.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));
        
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeactivateUser_Success() {
        savedUser.setStatus(User.UserStatus.ACTIVE);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setStatus(User.UserStatus.INACTIVE);
            return u;
        });

        UserDto result = userService.deactivateUser(1L);

        assertNotNull(result);
        assertEquals(User.UserStatus.INACTIVE, result.getStatus());
    }

    @Test
    void testDeleteUser_NotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(com.eb.eb_backend.exception.NotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
    }

    @Test
    void testActivateUser_Success() {
        savedUser.setStatus(User.UserStatus.PENDING);
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setStatus(User.UserStatus.ACTIVE);
            return u;
        });

        UserDto result = userService.activateUser(1L);

        assertNotNull(result);
        assertEquals(User.UserStatus.ACTIVE, result.getStatus());
    }
}

