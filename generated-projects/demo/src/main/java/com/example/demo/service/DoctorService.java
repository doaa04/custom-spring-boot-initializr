package com.example.demo.service;

import com.example.demo.dto.DoctorDto;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
List<DoctorDto> findAll();
    Optional<DoctorDto> findById(Long id);
        DoctorDto save(DoctorDto doctorDto);
        Optional<DoctorDto> update(Long id, DoctorDto doctorDto);
            boolean deleteById(Long id);
            }