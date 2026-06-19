package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.AuthResult;
import com.healthtrack.bmi.dto.LoginRequest;
import com.healthtrack.bmi.dto.RegisterRequest;
import com.healthtrack.bmi.entity.Doctor;
import com.healthtrack.bmi.mapper.DoctorMapper;
import com.healthtrack.bmi.repository.DoctorRepository;
import com.healthtrack.bmi.security.JwtUtils;
import com.healthtrack.bmi.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private Doctor doctor;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        doctor = Doctor.builder()
                .email("doctor@hospital.com")
                .passwordHash("hashedPassword")
                .name("Dr. House")
                .build();

        registerRequest = RegisterRequest.builder()
                .email("doctor@hospital.com")
                .password("password123")
                .name("Dr. House")
                .build();

        loginRequest = LoginRequest.builder()
                .email("doctor@hospital.com")
                .password("password123")
                .build();
    }

    @Test
    void testRegisterSuccessWhenNoDoctorExists() {
        when(doctorRepository.count()).thenReturn(0L);
        when(doctorRepository.existsByEmail(any())).thenReturn(false);
        when(doctorMapper.toEntity(any())).thenReturn(doctor);
        when(passwordEncoder.encode(any())).thenReturn("hashedPassword");
        when(doctorRepository.save(any())).thenReturn(doctor);
        when(jwtUtils.generateToken(any())).thenReturn("mockJwtToken");

        AuthResult result = authService.register(registerRequest);

        assertNotNull(result);
        assertEquals("mockJwtToken", result.getToken());
        assertEquals("doctor@hospital.com", result.getEmail());
        assertEquals("Dr. House", result.getName());
        
        verify(doctorRepository, times(1)).save(any());
    }

    @Test
    void testRegisterThrowsExceptionWhenDoctorAlreadyExists() {
        when(doctorRepository.count()).thenReturn(1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> 
            authService.register(registerRequest)
        );

        assertTrue(exception.getMessage().contains("Only one doctor account can exist"));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void testLoginSuccess() {
        when(doctorRepository.findByEmail("doctor@hospital.com")).thenReturn(Optional.of(doctor));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtils.generateToken("doctor@hospital.com")).thenReturn("mockJwtToken");

        AuthResult result = authService.login(loginRequest);

        assertNotNull(result);
        assertEquals("mockJwtToken", result.getToken());
        assertEquals("doctor@hospital.com", result.getEmail());
    }

    @Test
    void testLoginFailsWrongPassword() {
        when(doctorRepository.findByEmail("doctor@hospital.com")).thenReturn(Optional.of(doctor));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword")).thenReturn(false);

        loginRequest.setPassword("wrongPassword");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> 
            authService.login(loginRequest)
        );

        assertTrue(exception.getMessage().contains("Invalid email or password"));
    }
}
