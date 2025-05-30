package com.example.demo.service.impl;

import com.example.demo.dto.RandomDto;
import com.example.demo.entity.Random;
import com.example.demo.repository.RandomRepository;
import com.example.demo.service.RandomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class RandomServiceImpl implements RandomService {

private final RandomRepository randomRepository;

public RandomServiceImpl(RandomRepository randomRepository) {
this.randomRepository = randomRepository;
}

@Override
@Transactional(readOnly = true)
public List<RandomDto> findAll() {
    return randomRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RandomDto> findById(Long id) {
        return randomRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public RandomDto save(RandomDto randomDto) {
        Random random = convertToEntity(randomDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (randomDto.getId() == null) {
        random.setId(null); // Ensure it's null for auto-generation
        }
        random = randomRepository.save(random);
        return convertToDto(random);
        }

        @Override
        public Optional<RandomDto> update(Long id, RandomDto randomDto) {
            return randomRepository.findById(id)
            .map(existingRandom -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
            Random updatedRandom = randomRepository.save(existingRandom);
            return convertToDto(updatedRandom);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (randomRepository.existsById(id)) {
            randomRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private RandomDto convertToDto(Random random) {
            RandomDto dto = new RandomDto();
            BeanUtils.copyProperties(random, dto);
            return dto;
            }

            private Random convertToEntity(RandomDto randomDto) {
            Random entity = new Random();
            BeanUtils.copyProperties(randomDto, entity);
            return entity;
            }
            }