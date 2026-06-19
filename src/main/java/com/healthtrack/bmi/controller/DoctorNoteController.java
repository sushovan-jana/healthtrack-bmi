package com.healthtrack.bmi.controller;

import com.healthtrack.bmi.dto.DoctorNoteRequest;
import com.healthtrack.bmi.dto.DoctorNoteResponse;
import com.healthtrack.bmi.service.DoctorNoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorNoteController {

    private final DoctorNoteService doctorNoteService;

    @PostMapping("/patients/{id}/notes")
    public ResponseEntity<DoctorNoteResponse> addNote(
            @PathVariable("id") UUID patientId,
            @Valid @RequestBody DoctorNoteRequest request
    ) {
        DoctorNoteResponse response = doctorNoteService.addNote(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable("noteId") Long noteId
    ) {
        doctorNoteService.deleteNote(noteId);
        return ResponseEntity.noContent().build();
    }
}
