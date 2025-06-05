package com.example.demo.service.impl;

import com.example.demo.dto.DoctorDto;
import com.example.demo.entity.Doctor;
import com.example.demo.repository.DoctorRepository;
import com.example.demo.service.DoctorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class DoctorServiceImpl implements DoctorService {

private final DoctorRepository doctorRepository;

public DoctorServiceImpl(DoctorRepository doctorRepository) {
this.doctorRepository = doctorRepository;
}

@Override
@Transactional(readOnly = true)
public List<DoctorDto> findAll() {
    return doctorRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<DoctorDto> findById(Long id) {
        return doctorRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public DoctorDto save(DoctorDto doctorDto) {
        Doctor doctor = convertToEntity(doctorDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (doctorDto.getId() == null) {
        doctor.setId(null); // Ensure it's null for auto-generation
        }
        doctor = doctorRepository.save(doctor);
        return convertToDto(doctor);
        }

        @Override
        public Optional<DoctorDto> update(Long id, DoctorDto doctorDto) {
            return doctorRepository.findById(id)
            .map(existingDoctor -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (doctorDto.getFirstName() != null) { // Simple null check
                    existingDoctor.setFirstName(doctorDto.getFirstName());
                    }
                    if (doctorDto.getLastName() != null) { // Simple null check
                    existingDoctor.setLastName(doctorDto.getLastName());
                    }
                    if (doctorDto.getSpecialization() != null) { // Simple null check
                    existingDoctor.setSpecialization(doctorDto.getSpecialization());
                    }
                    if (doctorDto.getEmail() != null) { // Simple null check
                    existingDoctor.setEmail(doctorDto.getEmail());
                    }
                    if (doctorDto.getPhoneNumber() != null) { // Simple null check
                    existingDoctor.setPhoneNumber(doctorDto.getPhoneNumber());
                    }
            Doctor updatedDoctor = doctorRepository.save(existingDoctor);
            return convertToDto(updatedDoctor);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private DoctorDto convertToDto(Doctor doctor) {
            DoctorDto dto = new DoctorDto();
            BeanUtils.copyProperties(doctor, dto);
            return dto;
            }

            private Doctor convertToEntity(DoctorDto doctorDto) {
            Doctor entity = new Doctor();
            BeanUtils.copyProperties(doctorDto, entity);
            return entity;
            }
            }