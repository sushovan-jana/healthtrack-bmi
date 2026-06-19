package com.healthtrack.bmi.controller;

import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors/patients")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final PatientRepository patientRepository;

    @GetMapping(value = "/{id}/report", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable("id") UUID patientId
    ) {
        // Retrieve patient to formulate filename (throws 404 if not found)
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + patientId + " not found."));

        byte[] pdfBytes = reportService.generatePatientReport(patientId);

        // Formulate a clean filename, sanitizing spaces
        String safeName = patient.getName().replaceAll("[^a-zA-Z0-9]", "_");
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String filename = String.format("report_%s_%s.pdf", safeName, dateStr);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
