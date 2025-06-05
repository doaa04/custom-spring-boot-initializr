package com.example.demo.service.impl;

import com.example.demo.dto.MedicalRecordDto;
import com.example.demo.entity.MedicalRecord;
import com.example.demo.repository.MedicalRecordRepository;
import com.example.demo.service.MedicalRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class MedicalRecordServiceImpl implements MedicalRecordService {

private final MedicalRecordRepository medicalrecordRepository;

public MedicalRecordServiceImpl(MedicalRecordRepository medicalrecordRepository) {
this.medicalrecordRepository = medicalrecordRepository;
}

@Override
@Transactional(readOnly = true)
public List<MedicalRecordDto> findAll() {
    return medicalrecordRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicalRecordDto> findById(Long id) {
        return medicalrecordRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public MedicalRecordDto save(MedicalRecordDto medicalrecordDto) {
        MedicalRecord medicalrecord = convertToEntity(medicalrecordDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (medicalrecordDto.getId() == null) {
        medicalrecord.setId(null); // Ensure it's null for auto-generation
        }
        medicalrecord = medicalrecordRepository.save(medicalrecord);
        return convertToDto(medicalrecord);
        }

        @Override
        public Optional<MedicalRecordDto> update(Long id, MedicalRecordDto medicalrecordDto) {
            return medicalrecordRepository.findById(id)
            .map(existingMedicalRecord -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (medicalrecordDto.getRecordDate() != null) { // Simple null check
                    existingMedicalRecord.setRecordDate(medicalrecordDto.getRecordDate());
                    }
                    if (medicalrecordDto.getDescription() != null) { // Simple null check
                    existingMedicalRecord.setDescription(medicalrecordDto.getDescription());
                    }
                    if (medicalrecordDto.getDiagnosis() != null) { // Simple null check
                    existingMedicalRecord.setDiagnosis(medicalrecordDto.getDiagnosis());
                    }
                    if (medicalrecordDto.getTreatment() != null) { // Simple null check
                    existingMedicalRecord.setTreatment(medicalrecordDto.getTreatment());
                    }
                    if (medicalrecordDto.getPatientId() != null) { // Simple null check
                    existingMedicalRecord.setPatientId(medicalrecordDto.getPatientId());
                    }
                    if (medicalrecordDto.getDoctorId() != null) { // Simple null check
                    existingMedicalRecord.setDoctorId(medicalrecordDto.getDoctorId());
                    }
            MedicalRecord updatedMedicalRecord = medicalrecordRepository.save(existingMedicalRecord);
            return convertToDto(updatedMedicalRecord);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (medicalrecordRepository.existsById(id)) {
            medicalrecordRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private MedicalRecordDto convertToDto(MedicalRecord medicalrecord) {
            MedicalRecordDto dto = new MedicalRecordDto();
            BeanUtils.copyProperties(medicalrecord, dto);
            return dto;
            }

            private MedicalRecord convertToEntity(MedicalRecordDto medicalrecordDto) {
            MedicalRecord entity = new MedicalRecord();
            BeanUtils.copyProperties(medicalrecordDto, entity);
            return entity;
            }
            }