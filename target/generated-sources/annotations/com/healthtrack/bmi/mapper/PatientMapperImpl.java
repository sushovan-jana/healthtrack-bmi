package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.BmiCalculateRequest;
import com.healthtrack.bmi.dto.BmiCalculationResponse;
import com.healthtrack.bmi.dto.DoctorNoteResponse;
import com.healthtrack.bmi.dto.PatientDetailResponse;
import com.healthtrack.bmi.dto.PatientResponse;
import com.healthtrack.bmi.entity.BmiCalculation;
import com.healthtrack.bmi.entity.DoctorNote;
import com.healthtrack.bmi.entity.Patient;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T15:56:21+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class PatientMapperImpl implements PatientMapper {

    @Autowired
    private BmiCalculationMapper bmiCalculationMapper;
    @Autowired
    private DoctorNoteMapper doctorNoteMapper;

    @Override
    public PatientResponse toResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientResponse.PatientResponseBuilder patientResponse = PatientResponse.builder();

        patientResponse.id( patient.getId() );
        patientResponse.name( patient.getName() );
        patientResponse.phoneNumber( patient.getPhoneNumber() );
        patientResponse.age( patient.getAge() );
        patientResponse.gender( patient.getGender() );
        patientResponse.createdAt( patient.getCreatedAt() );
        patientResponse.updatedAt( patient.getUpdatedAt() );

        patientResponse.latestBmiValue( patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getBmiValue() );
        patientResponse.latestClassification( patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getClassification() );
        patientResponse.latestCalculatedAt( patient.getCalculations().isEmpty() ? null : patient.getCalculations().get(0).getCalculatedAt() );

        return patientResponse.build();
    }

    @Override
    public PatientDetailResponse toDetailResponse(Patient patient) {
        if ( patient == null ) {
            return null;
        }

        PatientDetailResponse.PatientDetailResponseBuilder patientDetailResponse = PatientDetailResponse.builder();

        patientDetailResponse.id( patient.getId() );
        patientDetailResponse.name( patient.getName() );
        patientDetailResponse.phoneNumber( patient.getPhoneNumber() );
        patientDetailResponse.age( patient.getAge() );
        patientDetailResponse.gender( patient.getGender() );
        patientDetailResponse.createdAt( patient.getCreatedAt() );
        patientDetailResponse.updatedAt( patient.getUpdatedAt() );
        patientDetailResponse.calculations( bmiCalculationListToBmiCalculationResponseList( patient.getCalculations() ) );
        patientDetailResponse.notes( doctorNoteListToDoctorNoteResponseList( patient.getNotes() ) );

        return patientDetailResponse.build();
    }

    @Override
    public Patient toEntity(BmiCalculateRequest request) {
        if ( request == null ) {
            return null;
        }

        Patient.PatientBuilder patient = Patient.builder();

        patient.phoneNumber( request.getPhone() );
        patient.name( request.getName() );
        patient.age( request.getAge() );
        patient.gender( request.getGender() );

        return patient.build();
    }

    protected List<BmiCalculationResponse> bmiCalculationListToBmiCalculationResponseList(List<BmiCalculation> list) {
        if ( list == null ) {
            return null;
        }

        List<BmiCalculationResponse> list1 = new ArrayList<BmiCalculationResponse>( list.size() );
        for ( BmiCalculation bmiCalculation : list ) {
            list1.add( bmiCalculationMapper.toResponse( bmiCalculation ) );
        }

        return list1;
    }

    protected List<DoctorNoteResponse> doctorNoteListToDoctorNoteResponseList(List<DoctorNote> list) {
        if ( list == null ) {
            return null;
        }

        List<DoctorNoteResponse> list1 = new ArrayList<DoctorNoteResponse>( list.size() );
        for ( DoctorNote doctorNote : list ) {
            list1.add( doctorNoteMapper.toResponse( doctorNote ) );
        }

        return list1;
    }
}
