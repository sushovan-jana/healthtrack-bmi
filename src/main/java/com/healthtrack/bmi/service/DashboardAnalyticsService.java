package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.BmiDistributionResponse;
import com.healthtrack.bmi.dto.DashboardAnalyticsResponse;
import com.healthtrack.bmi.dto.MonthlyTrendResponse;

import java.util.List;

public interface DashboardAnalyticsService {
    DashboardAnalyticsResponse getDashboardAnalytics();
    List<MonthlyTrendResponse> getPatientGrowthTrend();
    List<MonthlyTrendResponse> getBmiCalculationTrend();
    BmiDistributionResponse getBmiDistribution();
}
