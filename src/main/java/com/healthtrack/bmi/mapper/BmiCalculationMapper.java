package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.BmiCalculateResponse;
import com.healthtrack.bmi.dto.BmiCalculationResponse;
import com.healthtrack.bmi.dto.BmiTrendResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BmiCalculationMapper {

    BmiCalculationResponse toResponse(BmiCalculation calculation);

    BmiCalculateResponse toCalculateResponse(BmiCalculation calculation);

    BmiTrendResponse toTrendResponse(BmiCalculation calculation);
}
