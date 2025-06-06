package com.example.demo.service;

import com.example.demo.dto.GameSessionDto;
import java.util.List;
import java.util.Optional;

public interface GameSessionService {
List<GameSessionDto> findAll();
    Optional<GameSessionDto> findById(Long id);
        GameSessionDto save(GameSessionDto gamesessionDto);
        Optional<GameSessionDto> update(Long id, GameSessionDto gamesessionDto);
            boolean deleteById(Long id);
            }