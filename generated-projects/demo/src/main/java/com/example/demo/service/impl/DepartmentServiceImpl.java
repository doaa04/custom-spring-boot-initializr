package com.example.demo.service.impl;

import com.example.demo.dto.DepartmentDto;
import com.example.demo.entity.Department;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class DepartmentServiceImpl implements DepartmentService {

private final DepartmentRepository departmentRepository;

public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
this.departmentRepository = departmentRepository;
}

@Override
@Transactional(readOnly = true)
public List<DepartmentDto> findAll() {
    return departmentRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DepartmentDto> findById(Long id) {
        return departmentRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public DepartmentDto save(DepartmentDto departmentDto) {
        Department department = convertToEntity(departmentDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (departmentDto.getId() == null) {
        department.setId(null); // Ensure it's null for auto-generation
        }
        department = departmentRepository.save(department);
        return convertToDto(department);
        }

        @Override
        public Optional<DepartmentDto> update(Long id, DepartmentDto departmentDto) {
            return departmentRepository.findById(id)
            .map(existingDepartment -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (departmentDto.getName() != null) { // Simple null check
                    existingDepartment.setName(departmentDto.getName());
                    }
                    if (departmentDto.getLocation() != null) { // Simple null check
                    existingDepartment.setLocation(departmentDto.getLocation());
                    }
            Department updatedDepartment = departmentRepository.save(existingDepartment);
            return convertToDto(updatedDepartment);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private DepartmentDto convertToDto(Department department) {
            DepartmentDto dto = new DepartmentDto();
            BeanUtils.copyProperties(department, dto);
            return dto;
            }

            private Department convertToEntity(DepartmentDto departmentDto) {
            Department entity = new Department();
            BeanUtils.copyProperties(departmentDto, entity);
            return entity;
            }
            }