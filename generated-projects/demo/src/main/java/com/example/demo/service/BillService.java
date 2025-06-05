package com.example.demo.service;

import com.example.demo.dto.BillDto;
import java.util.List;
import java.util.Optional;

public interface BillService {
List<BillDto> findAll();
    Optional<BillDto> findById(Long id);
        BillDto save(BillDto billDto);
        Optional<BillDto> update(Long id, BillDto billDto);
            boolean deleteById(Long id);
            }