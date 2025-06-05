package com.example.demo.service;

import com.example.demo.dto.MedicalRecordDto;
import java.util.List;
import java.util.Optional;

public interface MedicalRecordService {
List<MedicalRecordDto> findAll();
    Optional<MedicalRecordDto> findById(Long id);
        MedicalRecordDto save(MedicalRecordDto medicalrecordDto);
        Optional<MedicalRecordDto> update(Long id, MedicalRecordDto medicalrecordDto);
            boolean deleteById(Long id);
            }