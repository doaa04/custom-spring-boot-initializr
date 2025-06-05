package com.example.demo.service.impl;

import com.example.demo.dto.StaffDto;
import com.example.demo.entity.Staff;
import com.example.demo.repository.StaffRepository;
import com.example.demo.service.StaffService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class StaffServiceImpl implements StaffService {

private final StaffRepository staffRepository;

public StaffServiceImpl(StaffRepository staffRepository) {
this.staffRepository = staffRepository;
}

@Override
@Transactional(readOnly = true)
public List<StaffDto> findAll() {
    return staffRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StaffDto> findById(Long id) {
        return staffRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public StaffDto save(StaffDto staffDto) {
        Staff staff = convertToEntity(staffDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (staffDto.getId() == null) {
        staff.setId(null); // Ensure it's null for auto-generation
        }
        staff = staffRepository.save(staff);
        return convertToDto(staff);
        }

        @Override
        public Optional<StaffDto> update(Long id, StaffDto staffDto) {
            return staffRepository.findById(id)
            .map(existingStaff -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (staffDto.getFirstName() != null) { // Simple null check
                    existingStaff.setFirstName(staffDto.getFirstName());
                    }
                    if (staffDto.getLastName() != null) { // Simple null check
                    existingStaff.setLastName(staffDto.getLastName());
                    }
                    if (staffDto.getRole() != null) { // Simple null check
                    existingStaff.setRole(staffDto.getRole());
                    }
                    if (staffDto.getEmail() != null) { // Simple null check
                    existingStaff.setEmail(staffDto.getEmail());
                    }
                    if (staffDto.getPhoneNumber() != null) { // Simple null check
                    existingStaff.setPhoneNumber(staffDto.getPhoneNumber());
                    }
                    if (staffDto.getDepartmentId() != null) { // Simple null check
                    existingStaff.setDepartmentId(staffDto.getDepartmentId());
                    }
            Staff updatedStaff = staffRepository.save(existingStaff);
            return convertToDto(updatedStaff);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private StaffDto convertToDto(Staff staff) {
            StaffDto dto = new StaffDto();
            BeanUtils.copyProperties(staff, dto);
            return dto;
            }

            private Staff convertToEntity(StaffDto staffDto) {
            Staff entity = new Staff();
            BeanUtils.copyProperties(staffDto, entity);
            return entity;
            }
            }