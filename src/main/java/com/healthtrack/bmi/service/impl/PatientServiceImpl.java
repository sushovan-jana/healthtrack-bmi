package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.BmiTrendResponse;
import com.healthtrack.bmi.dto.PatientDetailResponse;
import com.healthtrack.bmi.dto.PatientResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.mapper.BmiCalculationMapper;
import com.healthtrack.bmi.mapper.PatientMapper;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final BmiCalculationRepository bmiCalculationRepository;
    private final PatientMapper patientMapper;
    private final BmiCalculationMapper bmiCalculationMapper;

    @Override
    public Page<PatientResponse> searchPatients(String query, Pageable pageable) {
        Page<Patient> patients = patientRepository.searchPatients(query, pageable);
        return patients.map(patientMapper::toResponse);
    }

    @Override
    public PatientDetailResponse getPatientDetail(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + id + " not found."));
        
        if (!patient.getIsActive()) {
            throw new IllegalArgumentException("Patient has been deleted.");
        }
        
        return patientMapper.toDetailResponse(patient);
    }

    @Override
    public List<BmiTrendResponse> getPatientBmiTrend(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + id + " not found."));
        
        if (!patient.getIsActive()) {
            throw new IllegalArgumentException("Patient has been deleted.");
        }

        List<BmiCalculation> calculations = bmiCalculationRepository.findByPatientIdOrderByCalculatedAtAsc(id);
        return calculations.stream()
                .map(bmiCalculationMapper::toTrendResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + id + " not found."));
        
        patient.setIsActive(false);
        patient.setDeletedAt(Instant.now());
        patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void restorePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + id + " not found."));
        
        patient.setIsActive(true);
        patient.setDeletedAt(null);
        patientRepository.save(patient);
    }
}
