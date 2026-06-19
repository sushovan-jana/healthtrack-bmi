package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.BmiCalculateResponse;
import com.healthtrack.bmi.dto.BmiCalculationResponse;
import com.healthtrack.bmi.dto.BmiTrendResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T15:56:22+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class BmiCalculationMapperImpl implements BmiCalculationMapper {

    @Override
    public BmiCalculationResponse toResponse(BmiCalculation calculation) {
        if ( calculation == null ) {
            return null;
        }

        BmiCalculationResponse.BmiCalculationResponseBuilder bmiCalculationResponse = BmiCalculationResponse.builder();

        bmiCalculationResponse.id( calculation.getId() );
        bmiCalculationResponse.height( calculation.getHeight() );
        bmiCalculationResponse.weight( calculation.getWeight() );
        bmiCalculationResponse.bmiValue( calculation.getBmiValue() );
        bmiCalculationResponse.classification( calculation.getClassification() );
        bmiCalculationResponse.calculatedAt( calculation.getCalculatedAt() );

        return bmiCalculationResponse.build();
    }

    @Override
    public BmiCalculateResponse toCalculateResponse(BmiCalculation calculation) {
        if ( calculation == null ) {
            return null;
        }

        BmiCalculateResponse.BmiCalculateResponseBuilder bmiCalculateResponse = BmiCalculateResponse.builder();

        bmiCalculateResponse.bmiValue( calculation.getBmiValue() );
        bmiCalculateResponse.classification( calculation.getClassification() );
        bmiCalculateResponse.calculatedAt( calculation.getCalculatedAt() );

        return bmiCalculateResponse.build();
    }

    @Override
    public BmiTrendResponse toTrendResponse(BmiCalculation calculation) {
        if ( calculation == null ) {
            return null;
        }

        BmiTrendResponse.BmiTrendResponseBuilder bmiTrendResponse = BmiTrendResponse.builder();

        bmiTrendResponse.bmiValue( calculation.getBmiValue() );
        bmiTrendResponse.calculatedAt( calculation.getCalculatedAt() );

        return bmiTrendResponse.build();
    }
}
