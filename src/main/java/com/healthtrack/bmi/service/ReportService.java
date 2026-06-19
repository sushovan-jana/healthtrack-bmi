package com.healthtrack.bmi.service;

import java.util.UUID;

public interface ReportService {
    /**
     * Generates a PDF report for a patient containing demographics, history, trend charts, and notes.
     * @param patientId Patient ID
     * @return PDF byte array
     */
    byte[] generatePatientReport(UUID patientId);
}
