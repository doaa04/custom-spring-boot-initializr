package com.example.demo.service.impl;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.entity.Appointment;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.AppointmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class AppointmentServiceImpl implements AppointmentService {

private final AppointmentRepository appointmentRepository;

public AppointmentServiceImpl(AppointmentRepository appointmentRepository) {
this.appointmentRepository = appointmentRepository;
}

@Override
@Transactional(readOnly = true)
public List<AppointmentDto> findAll() {
    return appointmentRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppointmentDto> findById(Long id) {
        return appointmentRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public AppointmentDto save(AppointmentDto appointmentDto) {
        Appointment appointment = convertToEntity(appointmentDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (appointmentDto.getId() == null) {
        appointment.setId(null); // Ensure it's null for auto-generation
        }
        appointment = appointmentRepository.save(appointment);
        return convertToDto(appointment);
        }

        @Override
        public Optional<AppointmentDto> update(Long id, AppointmentDto appointmentDto) {
            return appointmentRepository.findById(id)
            .map(existingAppointment -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (appointmentDto.getAppointmentDate() != null) { // Simple null check
                    existingAppointment.setAppointmentDate(appointmentDto.getAppointmentDate());
                    }
                    if (appointmentDto.getAppointmentTime() != null) { // Simple null check
                    existingAppointment.setAppointmentTime(appointmentDto.getAppointmentTime());
                    }
                    if (appointmentDto.getReason() != null) { // Simple null check
                    existingAppointment.setReason(appointmentDto.getReason());
                    }
                    if (appointmentDto.getStatus() != null) { // Simple null check
                    existingAppointment.setStatus(appointmentDto.getStatus());
                    }
                    if (appointmentDto.getPatientId() != null) { // Simple null check
                    existingAppointment.setPatientId(appointmentDto.getPatientId());
                    }
                    if (appointmentDto.getDoctorId() != null) { // Simple null check
                    existingAppointment.setDoctorId(appointmentDto.getDoctorId());
                    }
            Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
            return convertToDto(updatedAppointment);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private AppointmentDto convertToDto(Appointment appointment) {
            AppointmentDto dto = new AppointmentDto();
            BeanUtils.copyProperties(appointment, dto);
            return dto;
            }

            private Appointment convertToEntity(AppointmentDto appointmentDto) {
            Appointment entity = new Appointment();
            BeanUtils.copyProperties(appointmentDto, entity);
            return entity;
            }
            }