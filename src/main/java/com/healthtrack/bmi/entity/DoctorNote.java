package com.healthtrack.bmi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "doctor_notes")
@EntityListeners(AuditingEntityListener.class) // Enable auditing listener
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank(message = "Note content cannot be empty")
    @Column(name = "note", nullable = false, columnDefinition = "TEXT")
    private String note;

    @CreatedDate // Spring Data auditing date
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate // Spring Data auditing date
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
