package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.AuthResult;
import com.healthtrack.bmi.dto.DoctorProfileResponse;
import com.healthtrack.bmi.dto.LoginRequest;
import com.healthtrack.bmi.dto.RegisterRequest;

import java.util.UUID;

public interface AuthService {
    AuthResult register(RegisterRequest request);
    AuthResult login(LoginRequest request);
    DoctorProfileResponse getProfile(UUID id);
    boolean isRegistrationAllowed();
}
