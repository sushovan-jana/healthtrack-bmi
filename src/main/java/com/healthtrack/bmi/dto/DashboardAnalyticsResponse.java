package com.healthtrack.bmi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsResponse {
    private Long totalPatients;
    private Long totalBmiCalculations;
    private Long todaysCalculations;
    private Double averageBmi;
    private Double highestBmi;
    private Double lowestBmi;
    private Long underweightPatientsCount;
    private Long normalWeightPatientsCount;
    private Long overweightPatientsCount;
    private Long obesePatientsCount;
}
