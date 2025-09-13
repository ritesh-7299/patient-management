package com.pm.patientservice.mapper;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.model.Patient;

import java.time.LocalDate;
import java.util.Date;

public class PatientMapper {
    public static PatientResponseDTO toDto(Patient p) {
        PatientResponseDTO dto = new PatientResponseDTO();
        dto.setId(p.getId().toString());
        dto.setName(p.getName());
        dto.setEmail(p.getEmail());
        dto.setAddress(p.getAddress());
        dto.setDateOfBirth(p.getDateOfBirth().toString());

        return dto;
    }

    public static Patient toModel(PatientRequestDTO dto) {
        Patient p = new Patient();
        p.setName(dto.getName());
        p.setAddress(dto.getAddress());
        p.setEmail(dto.getEmail());
        p.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        p.setRegisteredDate(LocalDate.now());

        return p;
    }
}
