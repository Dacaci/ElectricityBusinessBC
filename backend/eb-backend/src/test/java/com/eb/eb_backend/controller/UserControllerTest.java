package com.eb.eb_backend.controller;

import com.eb.eb_backend.dto.CreateUserDto;
import com.eb.eb_backend.dto.UserDto;
import com.eb.eb_backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com.eb.eb_backend.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUser_Success() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createUserDto.setEmail("john.doe@example.com");
        createUserDto.setPassword("password123");

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testCreateUser_EmailAlreadyExists() throws Exception {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createUserDto.setEmail("existing@example.com");
        createUserDto.setPassword("password123");

        when(userService.createUser(any(CreateUserDto.class)))
                .thenThrow(new com.eb.eb_backend.exception.ConflictException("Un utilisateur avec cet email existe déjà"));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testGetUserById_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");

        when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(userDto));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        when(userService.getUserById(999L)).thenReturn(java.util.Optional.empty());

        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers_WithSearch() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setEmail("john@example.com");
        user1.setFirstName("John");

        Page<UserDto> page = new PageImpl<>(List.of(user1), PageRequest.of(0, 10), 1);

        when(userService.getAllUsersOrSearch(eq("john"), any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                .param("q", "john")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].email").value("john@example.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAllUsers_WithoutSearch() throws Exception {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setEmail("test@example.com");

        Page<UserDto> page = new PageImpl<>(List.of(user1), PageRequest.of(0, 10), 1);

        when(userService.getAllUsersOrSearch(eq(null), any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("Updated");
        userDto.setLastName("Name");
        userDto.setEmail("updated@example.com");

        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}

