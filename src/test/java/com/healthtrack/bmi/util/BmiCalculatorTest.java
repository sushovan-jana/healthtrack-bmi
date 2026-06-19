package com.healthtrack.bmi.util;

import com.healthtrack.bmi.dto.UnitSystem;
import com.healthtrack.bmi.util.BmiCalculator.BmiResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BmiCalculatorTest {

    private BmiCalculator bmiCalculator;

    @BeforeEach
    void setUp() {
        bmiCalculator = new BmiCalculator();
    }

    @Test
    void testCalculateMetricNormalWeight() {
        // Height: 175 cm, Weight: 70 kg -> BMI: 70 / (1.75^2) = 22.86
        BmiResult result = bmiCalculator.calculate(175.0, 70.0, UnitSystem.METRIC);
        
        assertEquals(22.86, result.bmiValue());
        assertEquals("Normal weight", result.classification());
        assertEquals(175.0, result.heightCm());
        assertEquals(70.0, result.weightKg());
    }

    @Test
    void testCalculateImperialNormalWeight() {
        // Height: 68.9 inches (approx 175cm), Weight: 154.32 lbs (approx 70kg)
        // Expected height in cm: 68.9 * 2.54 = 175.006 cm
        // Expected weight in kg: 154.32 * 0.45359237 = 70.00007 kg
        // Expected BMI: 70.00007 / (1.75006^2) = 22.86
        BmiResult result = bmiCalculator.calculate(68.8976, 154.3236, UnitSystem.IMPERIAL);

        assertEquals(22.86, result.bmiValue());
        assertEquals("Normal weight", result.classification());
        assertTrue(Math.abs(result.heightCm() - 175.0) < 0.1);
        assertTrue(Math.abs(result.weightKg() - 70.0) < 0.1);
    }

    @Test
    void testWhoClassificationBoundaries() {
        // Severe thinness: < 16.0
        assertEquals("Severe thinness", bmiCalculator.getWhoClassification(15.9));
        
        // Moderate thinness: 16.0 - 16.99
        assertEquals("Moderate thinness", bmiCalculator.getWhoClassification(16.0));
        assertEquals("Moderate thinness", bmiCalculator.getWhoClassification(16.9));

        // Mild thinness: 17.0 - 18.49
        assertEquals("Mild thinness", bmiCalculator.getWhoClassification(17.0));
        assertEquals("Mild thinness", bmiCalculator.getWhoClassification(18.4));

        // Normal weight: 18.5 - 24.99
        assertEquals("Normal weight", bmiCalculator.getWhoClassification(18.5));
        assertEquals("Normal weight", bmiCalculator.getWhoClassification(24.9));

        // Overweight: 25.0 - 29.99
        assertEquals("Overweight", bmiCalculator.getWhoClassification(25.0));
        assertEquals("Overweight", bmiCalculator.getWhoClassification(29.9));

        // Obese Class I: 30.0 - 34.99
        assertEquals("Obese Class I", bmiCalculator.getWhoClassification(30.0));
        assertEquals("Obese Class I", bmiCalculator.getWhoClassification(34.9));

        // Obese Class II: 35.0 - 39.99
        assertEquals("Obese Class II", bmiCalculator.getWhoClassification(35.0));
        assertEquals("Obese Class II", bmiCalculator.getWhoClassification(39.9));

        // Obese Class III: >= 40.0
        assertEquals("Obese Class III", bmiCalculator.getWhoClassification(40.0));
        assertEquals("Obese Class III", bmiCalculator.getWhoClassification(52.5));
    }
}
