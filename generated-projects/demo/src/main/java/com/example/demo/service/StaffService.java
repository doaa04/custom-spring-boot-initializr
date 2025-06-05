package com.example.demo.service;

import com.example.demo.dto.StaffDto;
import java.util.List;
import java.util.Optional;

public interface StaffService {
List<StaffDto> findAll();
    Optional<StaffDto> findById(Long id);
        StaffDto save(StaffDto staffDto);
        Optional<StaffDto> update(Long id, StaffDto staffDto);
            boolean deleteById(Long id);
            }