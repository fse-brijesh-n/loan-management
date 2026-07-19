package com.loanmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.loanmanagement.dto.AuthDtos.AuthResponse;
import com.loanmanagement.dto.AuthDtos.LoginRequest;
import com.loanmanagement.dto.AuthDtos.RegisterRequest;
import com.loanmanagement.entity.Role;
import com.loanmanagement.entity.User;
import com.loanmanagement.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFullName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(Role.CUSTOMER);
    }

    @Test
    void registerCreatesCustomerAndReturnsToken() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "Password@123", Role.CUSTOMER, null);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.<User>any())).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.token());
        assertEquals(Role.CUSTOMER, response.role());
        assertEquals("john@example.com", response.email());
        verify(userRepository).save(org.mockito.ArgumentMatchers.<User>any());
    }

    @Test
    void registerCreatesAdminAndReturnsToken() {
        savedUser.setFullName("Admin User");
        savedUser.setEmail("admin@example.com");
        savedUser.setRole(Role.ADMIN);
        RegisterRequest request = new RegisterRequest("Admin User", "admin@example.com", "Password@123", Role.ADMIN, "Finance Department");

        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("encoded-password");
        when(userRepository.save(org.mockito.ArgumentMatchers.<User>any())).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertEquals("jwt-token", response.token());
        assertEquals(Role.ADMIN, response.role());
        assertEquals("admin@example.com", response.email());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "Password@123", Role.CUSTOMER, null);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.register(request));

        assertEquals("Email is already registered", exception.getMessage());
    }

    @Test
    void loginAuthenticatesAndReturnsToken() {
        LoginRequest request = new LoginRequest("john@example.com", "Password@123", Role.CUSTOMER);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(savedUser.getId(), response.userId());
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken("john@example.com", "Password@123"));
    }

    @Test
    void loginReturnsAdminAccessWhenAdminRoleSelected() {
        savedUser.setFullName("Admin User");
        savedUser.setEmail("admin@example.com");
        savedUser.setRole(Role.ADMIN);
        LoginRequest request = new LoginRequest("admin@example.com", "Password@123", Role.ADMIN);

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(savedUser));
        when(jwtService.generateToken(any(User.class), anyMap())).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        assertEquals(Role.ADMIN, response.role());
        assertEquals("admin@example.com", response.email());
    }

    @Test
    void loginRejectsWrongRoleSelection() {
        LoginRequest request = new LoginRequest("john@example.com", "Password@123", Role.ADMIN);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(savedUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.login(request));

        assertEquals("Invalid credentials", exception.getMessage());
    }
}