package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.BmiDistributionResponse;
import com.healthtrack.bmi.dto.DashboardAnalyticsResponse;
import com.healthtrack.bmi.dto.MonthlyTrendResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.DashboardAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardAnalyticsServiceImpl implements DashboardAnalyticsService {

    private final PatientRepository patientRepository;
    private final BmiCalculationRepository bmiCalculationRepository;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM")
            .withZone(ZoneId.systemDefault());

    @Override
    public DashboardAnalyticsResponse getDashboardAnalytics() {
        Instant startOfDay = LocalDate.now(ZoneId.systemDefault())
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        long totalPatients = patientRepository.count();
        long totalBmiCalculations = bmiCalculationRepository.count();
        long todaysCalculations = bmiCalculationRepository.countTodayCalculations(startOfDay);
        
        double rawAverageBmi = bmiCalculationRepository.getAverageBmi();
        double averageBmi = Math.round(rawAverageBmi * 100.0) / 100.0;

        double rawHighestBmi = bmiCalculationRepository.getHighestBmi();
        double highestBmi = Math.round(rawHighestBmi * 100.0) / 100.0;

        double rawLowestBmi = bmiCalculationRepository.getLowestBmi();
        double lowestBmi = Math.round(rawLowestBmi * 100.0) / 100.0;

        long underweightCount = bmiCalculationRepository.countUnderweightPatients();
        long normalWeightCount = bmiCalculationRepository.countNormalWeightPatients();
        long overweightCount = bmiCalculationRepository.countOverweightPatients();
        long obeseCount = bmiCalculationRepository.countObesePatients();

        return DashboardAnalyticsResponse.builder()
                .totalPatients(totalPatients)
                .totalBmiCalculations(totalBmiCalculations)
                .todaysCalculations(todaysCalculations)
                .averageBmi(averageBmi)
                .highestBmi(highestBmi)
                .lowestBmi(lowestBmi)
                .underweightPatientsCount(underweightCount)
                .normalWeightPatientsCount(normalWeightCount)
                .overweightPatientsCount(overweightCount)
                .obesePatientsCount(obeseCount)
                .build();
    }

    @Override
    public List<MonthlyTrendResponse> getPatientGrowthTrend() {
        List<Patient> patients = patientRepository.findAll();

        // Group active patients by creation month (YYYY-MM) in ascending order
        Map<String, Long> grouped = patients.stream()
                .filter(Patient::getIsActive)
                .collect(Collectors.groupingBy(
                        p -> MONTH_FORMATTER.format(p.getCreatedAt()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .map(e -> new MonthlyTrendResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTrendResponse> getBmiCalculationTrend() {
        List<BmiCalculation> calculations = bmiCalculationRepository.findAll();

        // Group all BMI calculations by date month (YYYY-MM) in ascending order
        Map<String, Long> grouped = calculations.stream()
                .collect(Collectors.groupingBy(
                        c -> MONTH_FORMATTER.format(c.getCalculatedAt()),
                        TreeMap::new,
                        Collectors.counting()
                ));

        return grouped.entrySet().stream()
                .map(e -> new MonthlyTrendResponse(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public BmiDistributionResponse getBmiDistribution() {
        long underweight = bmiCalculationRepository.countUnderweightPatients();
        long normal = bmiCalculationRepository.countNormalWeightPatients();
        long overweight = bmiCalculationRepository.countOverweightPatients();
        long obese = bmiCalculationRepository.countObesePatients();

        return BmiDistributionResponse.builder()
                .underweight(underweight)
                .normal(normal)
                .overweight(overweight)
                .obese(obese)
                .build();
    }
}
