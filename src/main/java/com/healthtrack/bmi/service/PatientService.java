package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.BmiTrendResponse;
import com.healthtrack.bmi.dto.PatientDetailResponse;
import com.healthtrack.bmi.dto.PatientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    Page<PatientResponse> searchPatients(String query, Pageable pageable);
    PatientDetailResponse getPatientDetail(UUID id);
    List<BmiTrendResponse> getPatientBmiTrend(UUID id);
    void deletePatient(UUID id);
    void restorePatient(UUID id);
}
