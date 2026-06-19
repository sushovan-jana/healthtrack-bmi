package com.healthtrack.bmi.repository;

import com.healthtrack.bmi.entity.BmiCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface BmiCalculationRepository extends JpaRepository<BmiCalculation, Long> {

    List<BmiCalculation> findByPatientIdOrderByCalculatedAtDesc(UUID patientId);

    List<BmiCalculation> findByPatientIdOrderByCalculatedAtAsc(UUID patientId); // Chronological order for trends charting

    /**
     * Count calculations performed today.
     */
    @Query("SELECT COUNT(c) FROM BmiCalculation c WHERE c.calculatedAt >= :startOfDay")
    long countTodayCalculations(@Param("startOfDay") Instant startOfDay);

    /**
     * Get average BMI across all historical calculations.
     */
    @Query("SELECT COALESCE(AVG(c.bmiValue), 0.0) FROM BmiCalculation c")
    double getAverageBmi();

    /**
     * Get the highest BMI value recorded globally.
     */
    @Query("SELECT COALESCE(MAX(c.bmiValue), 0.0) FROM BmiCalculation c")
    double getHighestBmi();

    /**
     * Get the lowest BMI value recorded globally.
     */
    @Query("SELECT COALESCE(MIN(c.bmiValue), 0.0) FROM BmiCalculation c")
    double getLowestBmi();

    /**
     * Count patients whose LATEST BMI calculation classifies them as 'Underweight'.
     * Maps WHO: 'Severe thinness', 'Moderate thinness', and 'Mild thinness'.
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE EXISTS (" +
           "SELECT c FROM BmiCalculation c WHERE c.patient = p " +
           "AND c.classification LIKE '%thinness' " +
           "AND c.calculatedAt = (SELECT MAX(c2.calculatedAt) FROM BmiCalculation c2 WHERE c2.patient = p)" +
           ")")
    long countUnderweightPatients();

    /**
     * Count patients whose LATEST BMI calculation classifies them as 'Normal weight'.
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE EXISTS (" +
           "SELECT c FROM BmiCalculation c WHERE c.patient = p " +
           "AND c.classification = 'Normal weight' " +
           "AND c.calculatedAt = (SELECT MAX(c2.calculatedAt) FROM BmiCalculation c2 WHERE c2.patient = p)" +
           ")")
    long countNormalWeightPatients();

    /**
     * Count patients whose LATEST BMI calculation classifies them as 'Overweight'.
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE EXISTS (" +
           "SELECT c FROM BmiCalculation c WHERE c.patient = p " +
           "AND c.classification = 'Overweight' " +
           "AND c.calculatedAt = (SELECT MAX(c2.calculatedAt) FROM BmiCalculation c2 WHERE c2.patient = p)" +
           ")")
    long countOverweightPatients();

    /**
     * Count patients whose LATEST BMI calculation classifies them as obese (Obese Class I, II, or III).
     */
    @Query("SELECT COUNT(p) FROM Patient p WHERE EXISTS (" +
           "SELECT c FROM BmiCalculation c WHERE c.patient = p " +
           "AND c.classification LIKE 'Obese%' " +
           "AND c.calculatedAt = (SELECT MAX(c2.calculatedAt) FROM BmiCalculation c2 WHERE c2.patient = p)" +
           ")")
    long countObesePatients();
}
