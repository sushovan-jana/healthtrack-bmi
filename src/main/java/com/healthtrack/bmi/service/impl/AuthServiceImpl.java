package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.AuthResult;
import com.healthtrack.bmi.dto.DoctorProfileResponse;
import com.healthtrack.bmi.dto.LoginRequest;
import com.healthtrack.bmi.dto.RegisterRequest;
import com.healthtrack.bmi.entity.Doctor;
import com.healthtrack.bmi.mapper.DoctorMapper;
import com.healthtrack.bmi.repository.DoctorRepository;
import com.healthtrack.bmi.security.JwtUtils;
import com.healthtrack.bmi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public AuthResult register(RegisterRequest request) {
        // Enforce single-doctor rule
        if (doctorRepository.count() > 0) {
            throw new IllegalStateException("Registration is disabled. Only one doctor account can exist in the system.");
        }

        if (doctorRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email address is already in use.");
        }

        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        Doctor savedDoctor = doctorRepository.save(doctor);
        String token = jwtUtils.generateToken(savedDoctor.getEmail());

        return AuthResult.builder()
                .token(token)
                .email(savedDoctor.getEmail())
                .name(savedDoctor.getName())
                .build();
    }

    @Override
    public AuthResult login(LoginRequest request) {
        Doctor doctor = doctorRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), doctor.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = jwtUtils.generateToken(doctor.getEmail());

        return AuthResult.builder()
                .token(token)
                .email(doctor.getEmail())
                .name(doctor.getName())
                .build();
    }

    @Override
    public DoctorProfileResponse getProfile(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found."));
        return doctorMapper.toProfileResponse(doctor);
    }

    @Override
    public boolean isRegistrationAllowed() {
        return doctorRepository.count() == 0;
    }
}
