package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.DoctorNoteRequest;
import com.healthtrack.bmi.dto.DoctorNoteResponse;
import com.healthtrack.bmi.entity.DoctorNote;
import com.healthtrack.bmi.entity.Patient;
import com.healthtrack.bmi.mapper.DoctorNoteMapper;
import com.healthtrack.bmi.repository.DoctorNoteRepository;
import com.healthtrack.bmi.repository.PatientRepository;
import com.healthtrack.bmi.service.DoctorNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorNoteServiceImpl implements DoctorNoteService {

    private final DoctorNoteRepository doctorNoteRepository;
    private final PatientRepository patientRepository;
    private final DoctorNoteMapper doctorNoteMapper;

    @Override
    @Transactional
    public DoctorNoteResponse addNote(UUID patientId, DoctorNoteRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient with ID " + patientId + " not found."));

        DoctorNote note = doctorNoteMapper.toEntity(request);
        note.setPatient(patient);

        DoctorNote savedNote = doctorNoteRepository.save(note);
        return doctorNoteMapper.toResponse(savedNote);
    }

    @Override
    public List<DoctorNoteResponse> getNotesByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new IllegalArgumentException("Patient with ID " + patientId + " not found.");
        }
        
        List<DoctorNote> notes = doctorNoteRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return notes.stream()
                .map(doctorNoteMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId) {
        if (!doctorNoteRepository.existsById(noteId)) {
            throw new IllegalArgumentException("Doctor note with ID " + noteId + " not found.");
        }
        doctorNoteRepository.deleteById(noteId);
    }
}
