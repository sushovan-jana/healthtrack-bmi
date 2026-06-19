package com.healthtrack.bmi.controller;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.BmiCalculateResponse;
import com.healthtrack.bmi.service.BmiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BmiPublicController {

    private final BmiService bmiService;

    @PostMapping("/api/bmi/calculate")
    public ResponseEntity<BmiCalculateResponse> calculateBmi(
            @Valid @RequestBody BmiCalculateRequest request
    ) {
        BmiCalculateResponse response = bmiService.calculateAndRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
