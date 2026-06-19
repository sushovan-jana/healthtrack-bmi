package com.healthtrack.bmi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "bmi_calculations")
@EntityListeners(AuditingEntityListener.class) // Enable auditing listener
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BmiCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotNull(message = "Height is required")
    @Min(value = 1, message = "Height must be greater than zero")
    @Column(name = "height", nullable = false)
    private Double height; // Stored in cm

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be greater than zero")
    @Column(name = "weight", nullable = false)
    private Double weight; // Stored in kg

    @NotNull(message = "BMI value is required")
    @Column(name = "bmi_value", nullable = false)
    private Double bmiValue;

    @NotBlank(message = "WHO classification is required")
    @Column(name = "classification", nullable = false)
    private String classification;

    @CreatedDate // Spring Data auditing date
    @Column(name = "calculated_at", nullable = false, updatable = false)
    private Instant calculatedAt;
}
