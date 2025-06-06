package com.example.demo.service;

import com.example.demo.dto.ObstacleDto;
import java.util.List;
import java.util.Optional;

public interface ObstacleService {
List<ObstacleDto> findAll();
    Optional<ObstacleDto> findById(Long id);
        ObstacleDto save(ObstacleDto obstacleDto);
        Optional<ObstacleDto> update(Long id, ObstacleDto obstacleDto);
            boolean deleteById(Long id);
            }