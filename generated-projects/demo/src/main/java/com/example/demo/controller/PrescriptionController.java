package com.example.demo.controller;

import com.example.demo.dto.PrescriptionDto;
import com.example.demo.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/prescriptions") // Simple pluralization
public class PrescriptionController {

private final PrescriptionService prescriptionService;

public PrescriptionController(PrescriptionService prescriptionService) {
this.prescriptionService = prescriptionService;
}

@GetMapping
public ResponseEntity<List<PrescriptionDto>> getAllPrescriptions() {
    return ResponseEntity.ok(prescriptionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable Long id) {
        return prescriptionService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<PrescriptionDto> createPrescription(@Valid @RequestBody PrescriptionDto prescriptionDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (prescriptionDto.getId() != null) {
            prescriptionDto.setId(null);
            }
            PrescriptionDto savedDto = prescriptionService.save(prescriptionDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<PrescriptionDto> updatePrescription(@PathVariable Long id, @Valid @RequestBody PrescriptionDto prescriptionDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (prescriptionDto.getId() == null) {
                prescriptionDto.setId(id);
                } else if (!prescriptionDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return prescriptionService.update(id, prescriptionDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
                    if (prescriptionService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found with id " + id);
                    }
                    }
                    }