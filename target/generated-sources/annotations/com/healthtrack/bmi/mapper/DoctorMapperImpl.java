package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.DoctorProfileResponse;
import com.healthtrack.bmi.dto.RegisterRequest;
import com.healthtrack.bmi.entity.Doctor;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T15:56:22+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class DoctorMapperImpl implements DoctorMapper {

    @Override
    public DoctorProfileResponse toProfileResponse(Doctor doctor) {
        if ( doctor == null ) {
            return null;
        }

        DoctorProfileResponse.DoctorProfileResponseBuilder doctorProfileResponse = DoctorProfileResponse.builder();

        doctorProfileResponse.id( doctor.getId() );
        doctorProfileResponse.email( doctor.getEmail() );
        doctorProfileResponse.name( doctor.getName() );
        doctorProfileResponse.createdAt( doctor.getCreatedAt() );
        doctorProfileResponse.updatedAt( doctor.getUpdatedAt() );

        return doctorProfileResponse.build();
    }

    @Override
    public Doctor toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        Doctor.DoctorBuilder doctor = Doctor.builder();

        doctor.email( request.getEmail() );
        doctor.name( request.getName() );

        return doctor.build();
    }
}
