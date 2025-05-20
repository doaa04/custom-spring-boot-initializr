package com.example.demo.service;

import com.example.demo.dto.SchoolDto;
import java.util.List;
import java.util.Optional;

public interface SchoolService {
List<SchoolDto> findAll();
    Optional<SchoolDto> findById(Long id);
        SchoolDto save(SchoolDto schoolDto);
        Optional<SchoolDto> update(Long id, SchoolDto schoolDto);
            boolean deleteById(Long id);
            }