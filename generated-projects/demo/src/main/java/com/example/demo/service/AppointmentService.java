package com.example.demo.service;

import com.example.demo.dto.AppointmentDto;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
List<AppointmentDto> findAll();
    Optional<AppointmentDto> findById(Long id);
        AppointmentDto save(AppointmentDto appointmentDto);
        Optional<AppointmentDto> update(Long id, AppointmentDto appointmentDto);
            boolean deleteById(Long id);
            }