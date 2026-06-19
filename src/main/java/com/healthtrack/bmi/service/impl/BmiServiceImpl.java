package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.BmiCalculateResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.mapper.BmiCalculationMapper;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.BmiService;
import com.healthtrack.bmi.util.BmiCalculator;
import com.healthtrack.bmi.util.BmiCalculator.BmiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BmiServiceImpl implements BmiService {

    private final PatientRepository patientRepository;
    private final BmiCalculationRepository bmiCalculationRepository;
    private final BmiCalculator bmiCalculator;
    private final BmiCalculationMapper bmiCalculationMapper;

    @Override
    @Transactional
    public BmiCalculateResponse calculateAndRecord(BmiCalculateRequest request) {
        // 1. Calculate BMI and classify according to WHO standards
        BmiResult result = bmiCalculator.calculate(
                request.getHeight(),
                request.getWeight(),
                request.getUnitSystem()
        );

        // 2. Look up or create the patient globally by phone number
        Optional<Patient> existingPatientOpt = patientRepository.findByPhoneNumber(request.getPhone());
        Patient patient;

        if (existingPatientOpt.isPresent()) {
            patient = existingPatientOpt.get();
            
            // Auto-restore patient if soft-deleted on new calculation submission
            if (!patient.getIsActive()) {
                patient.setIsActive(true);
                patient.setDeletedAt(null);
                log.info("Auto-restored soft-deleted patient with phone '{}' on new calculation submission.", patient.getPhoneNumber());
            }

            // Update name and age with latest inputs
            patient.setName(request.getName());
            patient.setAge(request.getAge());

            // Handle gender mismatch logging without overwriting
            if (!patient.getGender().equalsIgnoreCase(request.getGender())) {
                log.warn("Gender discrepancy detected for patient phone '{}'. Existing: '{}', Submitted: '{}'",
                        patient.getPhoneNumber(), patient.getGender(), request.getGender());
            }
        } else {
            // Register a new patient in the global namespace
            patient = Patient.builder()
                    .name(request.getName())
                    .phoneNumber(request.getPhone())
                    .age(request.getAge())
                    .gender(request.getGender())
                    .isActive(true)
                    .build();
        }

        // 3. Save the patient profile (creates or updates)
        Patient savedPatient = patientRepository.save(patient);

        // 4. Record the BMI calculation history entry (always metric normalized)
        BmiCalculation calculation = BmiCalculation.builder()
                .patient(savedPatient)
                .height(result.heightCm())
                .weight(result.weightKg())
                .bmiValue(result.bmiValue())
                .classification(result.classification())
                .build();

        BmiCalculation savedCalculation = bmiCalculationRepository.save(calculation);

        // 5. Convert and return the public response DTO
        return bmiCalculationMapper.toCalculateResponse(savedCalculation);
    }
}
