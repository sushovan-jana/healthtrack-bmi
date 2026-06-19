package com.healthtrack.bmi.service;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.BmiCalculateResponse;

public interface BmiService {
    BmiCalculateResponse calculateAndRecord(BmiCalculateRequest request);
}
