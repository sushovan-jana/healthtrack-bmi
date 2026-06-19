package com.healthtrack.bmi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorNoteRequest {

    @NotBlank(message = "Note content cannot be empty")
    @Size(max = 2000, message = "Note content must not exceed 2000 characters")
    private String note;
}
