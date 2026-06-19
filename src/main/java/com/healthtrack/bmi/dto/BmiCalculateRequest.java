package com.healthtrack.bmi.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiCalculateRequest {

    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(
        regexp = "^[+]?[0-9\\s\\-()]{7,20}$",
        message = "Invalid phone number format. Must contain 7 to 20 digits, optionally starting with '+'"
    )
    private String phone;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age cannot be negative")
    @Max(value = 150, message = "Please enter a realistic age")
    private Integer age;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotNull(message = "Height is required")
    @Min(value = 10, message = "Height must be at least 10")
    @Max(value = 300, message = "Height must not exceed 300")
    private Double height; // cm (metric) or inches (imperial)

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 1000, message = "Weight must not exceed 1000")
    private Double weight; // kg (metric) or lbs (imperial)

    @NotNull(message = "Unit system is required")
    private UnitSystem unitSystem;
}
