package com.healthtrack.bmi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiDistributionResponse {
    private Long underweight;
    private Long normal;
    private Long overweight;
    private Long obese;
}
