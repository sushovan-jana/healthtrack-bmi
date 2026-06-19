package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.ClinicSettings;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.DoctorNote;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.repository.BmiCalculationRepository;
import com.healthtrack.bmi.repository.DoctorNoteRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.ClinicSettingsProvider;
import com.healthtrack.bmi.service.ReportService;
import com.healthtrack.bmi.util.PdfGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PatientRepository patientRepository;
    private final BmiCalculationRepository bmiCalculationRepository;
    private final DoctorNoteRepository doctorNoteRepository;
    private final ClinicSettingsProvider clinicSettingsProvider;
    private final PdfGeneratorUtil pdfGeneratorUtil;

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final Random random = new Random();

    @Override
    public byte[] generatePatientReport(UUID patientId) {
        // 1. Fetch patient profile, throw exception if not found (Controller advice returns 404)
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + patientId + " not found."));

        // 2. Load historical calculation points and doctor notes
        List<BmiCalculation> calculations = bmiCalculationRepository.findByPatientIdOrderByCalculatedAtDesc(patientId);
        List<DoctorNote> notes = doctorNoteRepository.findByPatientIdOrderByCreatedAtDesc(patientId);

        // 3. Load branded clinic configuration properties
        ClinicSettings clinic = clinicSettingsProvider.getSettings();

        // 4. Generate unique report ID suffix (e.g. BMI-20260619-3549)
        String dateStr = LocalDate.now().format(DATE_PATTERN);
        int suffix = random.nextInt(10000);
        String reportId = String.format("BMI-%s-%04d", dateStr, suffix);

        // 5. Compile and export PDF document stream bytes
        try {
            return pdfGeneratorUtil.generateReport(patient, calculations, notes, clinic, reportId, Instant.now());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate chart image dependencies for PDF", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to build PDF report", e);
        }
    }
}
