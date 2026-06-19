package com.healthtrack.bmi.controller;

import com.healthtrack.bmi.dto.*;
import com.healthtrack.bmi.service.DashboardAnalyticsService;
import com.healthtrack.bmi.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final PatientService patientService;
    private final DashboardAnalyticsService dashboardAnalyticsService;

    /**
     * Search and list all active patients with pagination support.
     * Default sort: by patient name ascending.
     */
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientResponse>> getPatients(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<PatientResponse> response = patientService.searchPatients(search, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDetailResponse> getPatientDetail(
            @PathVariable("id") UUID id
    ) {
        PatientDetailResponse response = patientService.getPatientDetail(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Dedicated endpoint to retrieve a patient's historical calculations in chronological order
     * optimized for timeline line-chart rendering.
     */
    @GetMapping("/patients/{id}/bmi-trend")
    public ResponseEntity<List<BmiTrendResponse>> getPatientBmiTrend(
            @PathVariable("id") UUID id
    ) {
        List<BmiTrendResponse> response = patientService.getPatientBmiTrend(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Soft deletes a patient from the system directory.
     */
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable("id") UUID id
    ) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Restores a soft-deleted patient.
     */
    @PostMapping("/patients/{id}/restore")
    public ResponseEntity<Void> restorePatient(
            @PathVariable("id") UUID id
    ) {
        patientService.restorePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/analytics")
    public ResponseEntity<DashboardAnalyticsResponse> getDashboardAnalytics() {
        DashboardAnalyticsResponse response = dashboardAnalyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/patient-growth")
    public ResponseEntity<List<MonthlyTrendResponse>> getPatientGrowthTrend() {
        List<MonthlyTrendResponse> response = dashboardAnalyticsService.getPatientGrowthTrend();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/calculation-growth")
    public ResponseEntity<List<MonthlyTrendResponse>> getBmiCalculationTrend() {
        List<MonthlyTrendResponse> response = dashboardAnalyticsService.getBmiCalculationTrend();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/bmi-distribution")
    public ResponseEntity<BmiDistributionResponse> getBmiDistribution() {
        BmiDistributionResponse response = dashboardAnalyticsService.getBmiDistribution();
        return ResponseEntity.ok(response);
    }
}
