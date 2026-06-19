package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.DoctorNoteRequest;
import com.healthtrack.bmi.dto.DoctorNoteResponse;
import com.healthtrack.bmi.entity.DoctorNote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorNoteMapper {

    DoctorNoteResponse toResponse(DoctorNote note);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    DoctorNote toEntity(DoctorNoteRequest request);
}
