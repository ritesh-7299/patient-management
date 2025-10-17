package com.pm.patientservice.service;


import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.commonutils.exceptions.AlreadyExistException;
import com.pm.commonutils.exceptions.NotFoundException;
import com.pm.patientservice.grpc.BillingServiceGrpcClient;
import com.pm.patientservice.kafka.KafkaProducer;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    //Function to return the list of the patients
    public List<PatientResponseDTO> getAllPatients() {
        List<Patient> patients = repository.findAll();
        System.out.println(patients);
        return patients.stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDTO create(PatientRequestDTO dto) {
        Patient patient = PatientMapper.toModel(dto);
        repository.save(patient);

        billingServiceGrpcClient.createBillingAccount(
                patient.getId().toString(), patient.getName(), patient.getEmail()
        );

        kafkaProducer.sendMessage(patient);

        return PatientMapper.toDto(patient);
    }

    public PatientResponseDTO update(UUID id, PatientRequestDTO dto) {
        Patient patient = repository.findById(id).orElseThrow(() -> new NotFoundException(
                "Patient not found with id: " + id));

        patient.setName(dto.getName());
        patient.setAddress(dto.getAddress());
        patient.setEmail(dto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));
        repository.save(patient);
        return PatientMapper.toDto(patient);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
        billingServiceGrpcClient.deleteBillingAccount(id.toString());
    }
}
