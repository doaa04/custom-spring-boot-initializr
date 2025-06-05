package com.example.demo.service;

import com.example.demo.dto.PrescriptionDto;
import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
List<PrescriptionDto> findAll();
    Optional<PrescriptionDto> findById(Long id);
        PrescriptionDto save(PrescriptionDto prescriptionDto);
        Optional<PrescriptionDto> update(Long id, PrescriptionDto prescriptionDto);
            boolean deleteById(Long id);
            }