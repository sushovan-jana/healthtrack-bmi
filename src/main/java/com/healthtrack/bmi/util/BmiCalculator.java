package com.healthtrack.bmi.util;

import com.healthtrack.bmi.dto.UnitSystem;
import org.springframework.stereotype.Component;

@Component
public class BmiCalculator {

    private static final double INCH_TO_CM = 2.54;
    private static final double LB_TO_KG = 0.45359237;

    public record BmiResult(double bmiValue, String classification, double heightCm, double weightKg) {}

    /**
     * Calculates BMI value and categorizes it using WHO standards.
     * Normalizes height and weight measurements to Metric (cm, kg) if input is Imperial.
     */
    public BmiResult calculate(double height, double weight, UnitSystem unitSystem) {
        double heightCm;
        double weightKg;

        if (unitSystem == UnitSystem.IMPERIAL) {
            heightCm = height * INCH_TO_CM;
            weightKg = weight * LB_TO_KG;
        } else {
            heightCm = height;
            weightKg = weight;
        }

        // BMI Formula: weight (kg) / (height (m) ^ 2)
        double heightMeters = heightCm / 100.0;
        double bmiRaw = weightKg / (heightMeters * heightMeters);
        
        // Round to 2 decimal places
        double bmiValue = Math.round(bmiRaw * 100.0) / 100.0;
        
        String classification = getWhoClassification(bmiValue);

        return new BmiResult(bmiValue, classification, heightCm, weightKg);
    }

    /**
     * Evaluates BMI value against World Health Organization (WHO) BMI ranges.
     */
    public String getWhoClassification(double bmi) {
        if (bmi < 16.0) {
            return "Severe thinness";
        } else if (bmi < 17.0) {
            return "Moderate thinness";
        } else if (bmi < 18.5) {
            return "Mild thinness";
        } else if (bmi < 25.0) {
            return "Normal weight";
        } else if (bmi < 30.0) {
            return "Overweight";
        } else if (bmi < 35.0) {
            return "Obese Class I";
        } else if (bmi < 40.0) {
            return "Obese Class II";
        } else {
            return "Obese Class III";
        }
    }
}
