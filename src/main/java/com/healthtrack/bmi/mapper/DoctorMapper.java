package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.DoctorProfileResponse;
import com.healthtrack.bmi.dto.RegisterRequest;
import com.healthtrack.bmi.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorProfileResponse toProfileResponse(Doctor doctor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true) // Set manually in Service after password hashing
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Doctor toEntity(RegisterRequest request);
}
