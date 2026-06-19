package com.healthtrack.bmi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiCalculateResponse {
    private Double bmiValue;
    private String classification;
    private Instant calculatedAt;
}
