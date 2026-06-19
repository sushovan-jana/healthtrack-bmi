package com.healthtrack.bmi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patients")
@EntityListeners(AuditingEntityListener.class) // Enable auditing listener
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Patient name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Column(name = "gender", nullable = false)
    private String gender;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @CreatedDate // Spring Data auditing date
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate // Spring Data auditing date
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("calculatedAt DESC")
    @org.hibernate.annotations.BatchSize(size = 25)
    private List<BmiCalculation> calculations = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<DoctorNote> notes = new ArrayList<>();

    // Helper methods to maintain bidirectional integrity
    public void addCalculation(BmiCalculation calculation) {
        calculations.add(calculation);
        calculation.setPatient(this);
    }

    public void removeCalculation(BmiCalculation calculation) {
        calculations.remove(calculation);
        calculation.setPatient(null);
    }

    public void addNote(DoctorNote note) {
        notes.add(note);
        note.setPatient(this);
    }

    public void removeNote(DoctorNote note) {
        notes.remove(note);
        note.setPatient(null);
    }
}
