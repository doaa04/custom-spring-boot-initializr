package com.example.demo.service;

import com.example.demo.dto.DepartmentDto;
import java.util.List;
import java.util.Optional;

public interface DepartmentService {
List<DepartmentDto> findAll();
    Optional<DepartmentDto> findById(Long id);
        DepartmentDto save(DepartmentDto departmentDto);
        Optional<DepartmentDto> update(Long id, DepartmentDto departmentDto);
            boolean deleteById(Long id);
            }