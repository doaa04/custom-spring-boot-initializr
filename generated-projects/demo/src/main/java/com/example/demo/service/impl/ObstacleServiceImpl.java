package com.example.demo.service.impl;

import com.example.demo.dto.ObstacleDto;
import com.example.demo.entity.Obstacle;
import com.example.demo.repository.ObstacleRepository;
import com.example.demo.service.ObstacleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class ObstacleServiceImpl implements ObstacleService {

private final ObstacleRepository obstacleRepository;

public ObstacleServiceImpl(ObstacleRepository obstacleRepository) {
this.obstacleRepository = obstacleRepository;
}

@Override
@Transactional(readOnly = true)
public List<ObstacleDto> findAll() {
    return obstacleRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ObstacleDto> findById(Long id) {
        return obstacleRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public ObstacleDto save(ObstacleDto obstacleDto) {
        Obstacle obstacle = convertToEntity(obstacleDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (obstacleDto.getId() == null) {
        obstacle.setId(null); // Ensure it's null for auto-generation
        }
        obstacle = obstacleRepository.save(obstacle);
        return convertToDto(obstacle);
        }

        @Override
        public Optional<ObstacleDto> update(Long id, ObstacleDto obstacleDto) {
            return obstacleRepository.findById(id)
            .map(existingObstacle -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (obstacleDto.getType() != null) { // Simple null check
                    existingObstacle.setType(obstacleDto.getType());
                    }
                    if (obstacleDto.getPositionX() != null) { // Simple null check
                    existingObstacle.setPositionX(obstacleDto.getPositionX());
                    }
                    if (obstacleDto.getPositionY() != null) { // Simple null check
                    existingObstacle.setPositionY(obstacleDto.getPositionY());
                    }
            Obstacle updatedObstacle = obstacleRepository.save(existingObstacle);
            return convertToDto(updatedObstacle);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (obstacleRepository.existsById(id)) {
            obstacleRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private ObstacleDto convertToDto(Obstacle obstacle) {
            ObstacleDto dto = new ObstacleDto();
            BeanUtils.copyProperties(obstacle, dto);
            return dto;
            }

            private Obstacle convertToEntity(ObstacleDto obstacleDto) {
            Obstacle entity = new Obstacle();
            BeanUtils.copyProperties(obstacleDto, entity);
            return entity;
            }
            }