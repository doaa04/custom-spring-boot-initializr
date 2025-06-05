package com.example.demo.service.impl;

import com.example.demo.dto.BillDto;
import com.example.demo.entity.Bill;
import com.example.demo.repository.BillRepository;
import com.example.demo.service.BillService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class BillServiceImpl implements BillService {

private final BillRepository billRepository;

public BillServiceImpl(BillRepository billRepository) {
this.billRepository = billRepository;
}

@Override
@Transactional(readOnly = true)
public List<BillDto> findAll() {
    return billRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BillDto> findById(Long id) {
        return billRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public BillDto save(BillDto billDto) {
        Bill bill = convertToEntity(billDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (billDto.getId() == null) {
        bill.setId(null); // Ensure it's null for auto-generation
        }
        bill = billRepository.save(bill);
        return convertToDto(bill);
        }

        @Override
        public Optional<BillDto> update(Long id, BillDto billDto) {
            return billRepository.findById(id)
            .map(existingBill -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (billDto.getAmount() != null) { // Simple null check
                    existingBill.setAmount(billDto.getAmount());
                    }
                    if (billDto.getIssueDate() != null) { // Simple null check
                    existingBill.setIssueDate(billDto.getIssueDate());
                    }
                    if (billDto.getDueDate() != null) { // Simple null check
                    existingBill.setDueDate(billDto.getDueDate());
                    }
                    if (billDto.getStatus() != null) { // Simple null check
                    existingBill.setStatus(billDto.getStatus());
                    }
                    if (billDto.getPatientId() != null) { // Simple null check
                    existingBill.setPatientId(billDto.getPatientId());
                    }
            Bill updatedBill = billRepository.save(existingBill);
            return convertToDto(updatedBill);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (billRepository.existsById(id)) {
            billRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private BillDto convertToDto(Bill bill) {
            BillDto dto = new BillDto();
            BeanUtils.copyProperties(bill, dto);
            return dto;
            }

            private Bill convertToEntity(BillDto billDto) {
            Bill entity = new Bill();
            BeanUtils.copyProperties(billDto, entity);
            return entity;
            }
            }