package com.example.demo.service.impl;

import com.example.demo.dto.PrescriptionDto;
import com.example.demo.entity.Prescription;
import com.example.demo.repository.PrescriptionRepository;
import com.example.demo.service.PrescriptionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class PrescriptionServiceImpl implements PrescriptionService {

private final PrescriptionRepository prescriptionRepository;

public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
this.prescriptionRepository = prescriptionRepository;
}

@Override
@Transactional(readOnly = true)
public List<PrescriptionDto> findAll() {
    return prescriptionRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PrescriptionDto> findById(Long id) {
        return prescriptionRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public PrescriptionDto save(PrescriptionDto prescriptionDto) {
        Prescription prescription = convertToEntity(prescriptionDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (prescriptionDto.getId() == null) {
        prescription.setId(null); // Ensure it's null for auto-generation
        }
        prescription = prescriptionRepository.save(prescription);
        return convertToDto(prescription);
        }

        @Override
        public Optional<PrescriptionDto> update(Long id, PrescriptionDto prescriptionDto) {
            return prescriptionRepository.findById(id)
            .map(existingPrescription -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (prescriptionDto.getDateIssued() != null) { // Simple null check
                    existingPrescription.setDateIssued(prescriptionDto.getDateIssued());
                    }
                    if (prescriptionDto.getMedicationDetails() != null) { // Simple null check
                    existingPrescription.setMedicationDetails(prescriptionDto.getMedicationDetails());
                    }
                    if (prescriptionDto.getDosageInstructions() != null) { // Simple null check
                    existingPrescription.setDosageInstructions(prescriptionDto.getDosageInstructions());
                    }
                    if (prescriptionDto.getPatientId() != null) { // Simple null check
                    existingPrescription.setPatientId(prescriptionDto.getPatientId());
                    }
                    if (prescriptionDto.getDoctorId() != null) { // Simple null check
                    existingPrescription.setDoctorId(prescriptionDto.getDoctorId());
                    }
            Prescription updatedPrescription = prescriptionRepository.save(existingPrescription);
            return convertToDto(updatedPrescription);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private PrescriptionDto convertToDto(Prescription prescription) {
            PrescriptionDto dto = new PrescriptionDto();
            BeanUtils.copyProperties(prescription, dto);
            return dto;
            }

            private Prescription convertToEntity(PrescriptionDto prescriptionDto) {
            Prescription entity = new Prescription();
            BeanUtils.copyProperties(prescriptionDto, entity);
            return entity;
            }
            }