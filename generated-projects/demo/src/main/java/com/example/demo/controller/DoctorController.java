package com.example.demo.controller;

import com.example.demo.dto.DoctorDto;
import com.example.demo.service.DoctorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/doctors") // Simple pluralization
public class DoctorController {

private final DoctorService doctorService;

public DoctorController(DoctorService doctorService) {
this.doctorService = doctorService;
}

@GetMapping
public ResponseEntity<List<DoctorDto>> getAllDoctors() {
    return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorDto> getDoctorById(@PathVariable Long id) {
        return doctorService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<DoctorDto> createDoctor(@Valid @RequestBody DoctorDto doctorDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (doctorDto.getId() != null) {
            doctorDto.setId(null);
            }
            DoctorDto savedDto = doctorService.save(doctorDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<DoctorDto> updateDoctor(@PathVariable Long id, @Valid @RequestBody DoctorDto doctorDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (doctorDto.getId() == null) {
                doctorDto.setId(id);
                } else if (!doctorDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return doctorService.update(id, doctorDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
                    if (doctorService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found with id " + id);
                    }
                    }
                    }