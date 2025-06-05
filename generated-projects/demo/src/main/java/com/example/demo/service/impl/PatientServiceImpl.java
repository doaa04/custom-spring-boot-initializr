package com.example.demo.service.impl;

import com.example.demo.dto.PatientDto;
import com.example.demo.entity.Patient;
import com.example.demo.repository.PatientRepository;
import com.example.demo.service.PatientService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class PatientServiceImpl implements PatientService {

private final PatientRepository patientRepository;

public PatientServiceImpl(PatientRepository patientRepository) {
this.patientRepository = patientRepository;
}

@Override
@Transactional(readOnly = true)
public List<PatientDto> findAll() {
    return patientRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PatientDto> findById(Long id) {
        return patientRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public PatientDto save(PatientDto patientDto) {
        Patient patient = convertToEntity(patientDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (patientDto.getId() == null) {
        patient.setId(null); // Ensure it's null for auto-generation
        }
        patient = patientRepository.save(patient);
        return convertToDto(patient);
        }

        @Override
        public Optional<PatientDto> update(Long id, PatientDto patientDto) {
            return patientRepository.findById(id)
            .map(existingPatient -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (patientDto.getFirstName() != null) { // Simple null check
                    existingPatient.setFirstName(patientDto.getFirstName());
                    }
                    if (patientDto.getLastName() != null) { // Simple null check
                    existingPatient.setLastName(patientDto.getLastName());
                    }
                    if (patientDto.getDateOfBirth() != null) { // Simple null check
                    existingPatient.setDateOfBirth(patientDto.getDateOfBirth());
                    }
                    if (patientDto.getGender() != null) { // Simple null check
                    existingPatient.setGender(patientDto.getGender());
                    }
                    if (patientDto.getPhoneNumber() != null) { // Simple null check
                    existingPatient.setPhoneNumber(patientDto.getPhoneNumber());
                    }
                    if (patientDto.getEmail() != null) { // Simple null check
                    existingPatient.setEmail(patientDto.getEmail());
                    }
                    if (patientDto.getAddress() != null) { // Simple null check
                    existingPatient.setAddress(patientDto.getAddress());
                    }
            Patient updatedPatient = patientRepository.save(existingPatient);
            return convertToDto(updatedPatient);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private PatientDto convertToDto(Patient patient) {
            PatientDto dto = new PatientDto();
            BeanUtils.copyProperties(patient, dto);
            return dto;
            }

            private Patient convertToEntity(PatientDto patientDto) {
            Patient entity = new Patient();
            BeanUtils.copyProperties(patientDto, entity);
            return entity;
            }
            }