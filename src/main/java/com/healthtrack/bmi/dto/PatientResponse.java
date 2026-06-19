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
public class PatientResponse {
    private UUID id;
    private String name;
    private String phoneNumber;
    private Integer age;
    private String gender;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Latest diagnostic calculation values (bound to UI directory card renderer)
    private Double latestBmiValue;
    private String latestClassification;
    private Instant latestCalculatedAt;
}
