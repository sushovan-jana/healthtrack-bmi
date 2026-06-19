package com.healthtrack.bmi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDetailResponse {
    private UUID id;
    private String name;
    private String phoneNumber;
    private Integer age;
    private String gender;
    private Instant createdAt;
    private Instant updatedAt;
    private List<BmiCalculationResponse> calculations;
    private List<DoctorNoteResponse> notes;
}
