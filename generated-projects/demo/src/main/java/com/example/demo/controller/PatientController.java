package com.example.demo.controller;

import com.example.demo.dto.PatientDto;
import com.example.demo.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/patients") // Simple pluralization
public class PatientController {

private final PatientService patientService;

public PatientController(PatientService patientService) {
this.patientService = patientService;
}

@GetMapping
public ResponseEntity<List<PatientDto>> getAllPatients() {
    return ResponseEntity.ok(patientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable Long id) {
        return patientService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody PatientDto patientDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (patientDto.getId() != null) {
            patientDto.setId(null);
            }
            PatientDto savedDto = patientService.save(patientDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<PatientDto> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDto patientDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (patientDto.getId() == null) {
                patientDto.setId(id);
                } else if (!patientDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return patientService.update(id, patientDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
                    if (patientService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found with id " + id);
                    }
                    }
                    }