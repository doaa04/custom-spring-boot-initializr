package com.example.demo.controller;

import com.example.demo.dto.AppointmentDto;
import com.example.demo.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/appointments") // Simple pluralization
public class AppointmentController {

private final AppointmentService appointmentService;

public AppointmentController(AppointmentService appointmentService) {
this.appointmentService = appointmentService;
}

@GetMapping
public ResponseEntity<List<AppointmentDto>> getAllAppointments() {
    return ResponseEntity.ok(appointmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        return appointmentService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto appointmentDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (appointmentDto.getId() != null) {
            appointmentDto.setId(null);
            }
            AppointmentDto savedDto = appointmentService.save(appointmentDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDto appointmentDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (appointmentDto.getId() == null) {
                appointmentDto.setId(id);
                } else if (!appointmentDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return appointmentService.update(id, appointmentDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
                    if (appointmentService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found with id " + id);
                    }
                    }
                    }