package com.example.demo.controller;

import com.example.demo.dto.BillDto;
import com.example.demo.service.BillService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/bills") // Simple pluralization
public class BillController {

private final BillService billService;

public BillController(BillService billService) {
this.billService = billService;
}

@GetMapping
public ResponseEntity<List<BillDto>> getAllBills() {
    return ResponseEntity.ok(billService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDto> getBillById(@PathVariable Long id) {
        return billService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<BillDto> createBill(@Valid @RequestBody BillDto billDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (billDto.getId() != null) {
            billDto.setId(null);
            }
            BillDto savedDto = billService.save(billDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<BillDto> updateBill(@PathVariable Long id, @Valid @RequestBody BillDto billDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (billDto.getId() == null) {
                billDto.setId(id);
                } else if (!billDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return billService.update(id, billDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
                    if (billService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bill not found with id " + id);
                    }
                    }
                    }