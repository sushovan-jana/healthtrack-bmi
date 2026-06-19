package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.BmiCalculateResponse;
import com.healthtrack.bmi.dto.UnitSystem;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.mapper.BmiCalculationMapper;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.impl.BmiServiceImpl;
import com.healthtrack.bmi.util.BmiCalculator;
import com.healthtrack.bmi.util.BmiCalculator.BmiResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BmiServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private BmiCalculationRepository bmiCalculationRepository;

    @Mock
    private BmiCalculator bmiCalculator;

    @Mock
    private BmiCalculationMapper bmiCalculationMapper;

    @InjectMocks
    private BmiServiceImpl bmiService;

    private BmiCalculateRequest request;
    private Patient patient;
    private BmiResult bmiResult;
    private BmiCalculation calculation;

    @BeforeEach
    void setUp() {
        request = BmiCalculateRequest.builder()
                .name("Jane Doe")
                .phone("5550199")
                .age(28)
                .gender("Female")
                .height(168.0)
                .weight(62.5)
                .unitSystem(UnitSystem.METRIC)
                .build();

        patient = Patient.builder()
                .name("Jane Doe")
                .phoneNumber("5550199")
                .age(28)
                .gender("Female")
                .build();

        bmiResult = new BmiResult(22.15, "Normal weight", 168.0, 62.5);

        calculation = BmiCalculation.builder()
                .patient(patient)
                .height(168.0)
                .weight(62.5)
                .bmiValue(22.15)
                .classification("Normal weight")
                .build();
    }

    @Test
    void testCalculateAndRecordNewPatient() {
        // Arrange
        when(bmiCalculator.calculate(168.0, 62.5, UnitSystem.METRIC)).thenReturn(bmiResult);
        when(patientRepository.findByPhoneNumber("5550199")).thenReturn(Optional.empty());
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);
        when(bmiCalculationRepository.save(any(BmiCalculation.class))).thenReturn(calculation);
        
        BmiCalculateResponse mockResponse = BmiCalculateResponse.builder()
                .bmiValue(22.15)
                .classification("Normal weight")
                .build();
        when(bmiCalculationMapper.toCalculateResponse(any(BmiCalculation.class))).thenReturn(mockResponse);

        // Act
        BmiCalculateResponse response = bmiService.calculateAndRecord(request);

        // Assert
        assertNotNull(response);
        assertEquals(22.15, response.getBmiValue());
        assertEquals("Normal weight", response.getClassification());

        verify(patientRepository, times(1)).save(any(Patient.class));
        verify(bmiCalculationRepository, times(1)).save(any(BmiCalculation.class));
    }

    @Test
    void testCalculateAndRecordExistingPatientWithUpdates() {
        // Arrange
        Patient existingPatient = Patient.builder()
                .name("Jane OldName")
                .phoneNumber("5550199")
                .age(27)
                .gender("Female")
                .build();

        when(bmiCalculator.calculate(168.0, 62.5, UnitSystem.METRIC)).thenReturn(bmiResult);
        when(patientRepository.findByPhoneNumber("5550199")).thenReturn(Optional.of(existingPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bmiCalculationRepository.save(any(BmiCalculation.class))).thenReturn(calculation);
        
        BmiCalculateResponse mockResponse = BmiCalculateResponse.builder()
                .bmiValue(22.15)
                .classification("Normal weight")
                .build();
        when(bmiCalculationMapper.toCalculateResponse(any(BmiCalculation.class))).thenReturn(mockResponse);

        // Act
        bmiService.calculateAndRecord(request);

        // Assert
        // Verify name and age updated to request values
        assertEquals("Jane Doe", existingPatient.getName());
        assertEquals(28, existingPatient.getAge());
        // Verify gender not changed
        assertEquals("Female", existingPatient.getGender());

        verify(patientRepository, times(1)).save(existingPatient);
    }
}
