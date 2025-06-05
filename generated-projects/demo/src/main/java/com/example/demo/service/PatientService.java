package com.example.demo.service;

import com.example.demo.dto.PatientDto;
import java.util.List;
import java.util.Optional;

public interface PatientService {
List<PatientDto> findAll();
    Optional<PatientDto> findById(Long id);
        PatientDto save(PatientDto patientDto);
        Optional<PatientDto> update(Long id, PatientDto patientDto);
            boolean deleteById(Long id);
            }