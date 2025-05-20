package com.example.demo.service.impl;

import com.example.demo.dto.SchoolDto;
import com.example.demo.entity.School;
import com.example.demo.repository.SchoolRepository;
import com.example.demo.service.SchoolService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class SchoolServiceImpl implements SchoolService {

private final SchoolRepository schoolRepository;

public SchoolServiceImpl(SchoolRepository schoolRepository) {
this.schoolRepository = schoolRepository;
}

@Override
@Transactional(readOnly = true)
public List<SchoolDto> findAll() {
    return schoolRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SchoolDto> findById(Long id) {
        return schoolRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public SchoolDto save(SchoolDto schoolDto) {
        School school = convertToEntity(schoolDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (schoolDto.getId() == null) {
        school.setId(null); // Ensure it's null for auto-generation
        }
        school = schoolRepository.save(school);
        return convertToDto(school);
        }

        @Override
        public Optional<SchoolDto> update(Long id, SchoolDto schoolDto) {
            return schoolRepository.findById(id)
            .map(existingSchool -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (schoolDto.getName() != null) { // Simple null check
                    existingSchool.setName(schoolDto.getName());
                    }
                    if (schoolDto.getLocation() != null) { // Simple null check
                    existingSchool.setLocation(schoolDto.getLocation());
                    }
            School updatedSchool = schoolRepository.save(existingSchool);
            return convertToDto(updatedSchool);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (schoolRepository.existsById(id)) {
            schoolRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private SchoolDto convertToDto(School school) {
            SchoolDto dto = new SchoolDto();
            BeanUtils.copyProperties(school, dto);
            return dto;
            }

            private School convertToEntity(SchoolDto schoolDto) {
            School entity = new School();
            BeanUtils.copyProperties(schoolDto, entity);
            return entity;
            }
            }