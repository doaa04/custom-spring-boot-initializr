package com.example.demo.controller;

import com.example.demo.dto.MedicalRecordDto;
import com.example.demo.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/medicalrecords") // Simple pluralization
public class MedicalRecordController {

private final MedicalRecordService medicalrecordService;

public MedicalRecordController(MedicalRecordService medicalrecordService) {
this.medicalrecordService = medicalrecordService;
}

@GetMapping
public ResponseEntity<List<MedicalRecordDto>> getAllMedicalRecords() {
    return ResponseEntity.ok(medicalrecordService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecordDto> getMedicalRecordById(@PathVariable Long id) {
        return medicalrecordService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MedicalRecord not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<MedicalRecordDto> createMedicalRecord(@Valid @RequestBody MedicalRecordDto medicalrecordDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (medicalrecordDto.getId() != null) {
            medicalrecordDto.setId(null);
            }
            MedicalRecordDto savedDto = medicalrecordService.save(medicalrecordDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<MedicalRecordDto> updateMedicalRecord(@PathVariable Long id, @Valid @RequestBody MedicalRecordDto medicalrecordDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (medicalrecordDto.getId() == null) {
                medicalrecordDto.setId(id);
                } else if (!medicalrecordDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return medicalrecordService.update(id, medicalrecordDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MedicalRecord not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
                    if (medicalrecordService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MedicalRecord not found with id " + id);
                    }
                    }
                    }