package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.DoctorNoteRequest;
import com.healthtrack.bmi.dto.DoctorNoteResponse;

import java.util.List;
import java.util.UUID;

public interface DoctorNoteService {
    DoctorNoteResponse addNote(UUID patientId, DoctorNoteRequest request);
    List<DoctorNoteResponse> getNotesByPatient(UUID patientId);
    void deleteNote(Long noteId);
}
