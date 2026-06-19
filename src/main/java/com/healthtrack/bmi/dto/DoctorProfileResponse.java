package com.healthtrack.bmi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfileResponse {
    private UUID id;
    private String email;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
    private String token; // JWT token returned on login/register for localStorage auth
}

