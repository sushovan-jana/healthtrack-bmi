package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.PatientDetailResponse;
import com.healthtrack.bmi.dto.PatientResponse;
import com.healthtrack.bmi.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { BmiCalculationMapper.class, DoctorNoteMapper.class })
public interface PatientMapper {

    @Mapping(target = "latestBmiValue", expression = "java(patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getBmiValue())")
    @Mapping(target = "latestClassification", expression = "java(patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getClassification())")
    @Mapping(target = "latestCalculatedAt", expression = "java(patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getCalculatedAt())")
    PatientResponse toResponse(Patient patient);

    PatientDetailResponse toDetailResponse(Patient patient);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneNumber", source = "phone")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "calculations", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Patient toEntity(BmiCalculateRequest request);
}
