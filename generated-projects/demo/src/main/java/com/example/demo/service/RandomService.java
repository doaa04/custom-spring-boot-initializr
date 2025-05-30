package com.example.demo.service;

import com.example.demo.dto.RandomDto;
import java.util.List;
import java.util.Optional;

public interface RandomService {
List<RandomDto> findAll();
    Optional<RandomDto> findById(Long id);
        RandomDto save(RandomDto randomDto);
        Optional<RandomDto> update(Long id, RandomDto randomDto);
            boolean deleteById(Long id);
            }