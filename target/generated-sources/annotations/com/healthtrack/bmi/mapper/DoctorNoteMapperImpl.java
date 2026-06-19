package com.healthtrack.bmi.mapper;

import com.healthtrack.bmi.dto.DoctorNoteRequest;
import com.healthtrack.bmi.dto.DoctorNoteResponse;
import com.healthtrack.bmi.entity.DoctorNote;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-19T15:56:22+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Eclipse Adoptium)"
)
@Component
public class DoctorNoteMapperImpl implements DoctorNoteMapper {

    @Override
    public DoctorNoteResponse toResponse(DoctorNote note) {
        if ( note == null ) {
            return null;
        }

        DoctorNoteResponse.DoctorNoteResponseBuilder doctorNoteResponse = DoctorNoteResponse.builder();

        doctorNoteResponse.id( note.getId() );
        doctorNoteResponse.note( note.getNote() );
        doctorNoteResponse.createdAt( note.getCreatedAt() );
        doctorNoteResponse.updatedAt( note.getUpdatedAt() );

        return doctorNoteResponse.build();
    }

    @Override
    public DoctorNote toEntity(DoctorNoteRequest request) {
        if ( request == null ) {
            return null;
        }

        DoctorNote.DoctorNoteBuilder doctorNote = DoctorNote.builder();

        doctorNote.note( request.getNote() );

        return doctorNote.build();
    }
}
