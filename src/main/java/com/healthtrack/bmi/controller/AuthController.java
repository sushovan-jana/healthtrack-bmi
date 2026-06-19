package com.healthtrack.bmi.controller;

import com.healthtrack.bmi.dto.*;
import com.healthtrack.bmi.security.JwtUtils;
import com.healthtrack.bmi.security.UserDetailsImpl;
import com.healthtrack.bmi.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${healthtrack.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${healthtrack.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @PostMapping("/api/auth/register")
    public ResponseEntity<DoctorProfileResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        AuthResult result = authService.register(request);
        setCookie(response, result.getToken(), jwtExpirationMs / 1000);

        DoctorProfileResponse profile = DoctorProfileResponse.builder()
                .email(result.getEmail())
                .name(result.getName())
                .token(result.getToken()) // Return token for localStorage fallback
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<DoctorProfileResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthResult result = authService.login(request);
        setCookie(response, result.getToken(), jwtExpirationMs / 1000);

        DoctorProfileResponse profile = DoctorProfileResponse.builder()
                .email(result.getEmail())
                .name(result.getName())
                .token(result.getToken()) // Return token for localStorage fallback
                .build();

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/api/doctors/auth/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear JWT token cookie
        setCookie(response, "", 0L);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/doctors/auth/me")
    public ResponseEntity<DoctorProfileResponse> getMe(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        DoctorProfileResponse profile = DoctorProfileResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .name(userDetails.getName())
                .build();
        return ResponseEntity.ok(profile);
    }

    private void setCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(JwtUtils.COOKIE_NAME, token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(maxAgeSeconds)
                .sameSite("None")
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
