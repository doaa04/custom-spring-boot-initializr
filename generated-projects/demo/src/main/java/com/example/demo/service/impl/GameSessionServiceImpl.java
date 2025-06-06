package com.example.demo.service.impl;

import com.example.demo.dto.GameSessionDto;
import com.example.demo.entity.GameSession;
import com.example.demo.repository.GameSessionRepository;
import com.example.demo.service.GameSessionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class GameSessionServiceImpl implements GameSessionService {

private final GameSessionRepository gamesessionRepository;

public GameSessionServiceImpl(GameSessionRepository gamesessionRepository) {
this.gamesessionRepository = gamesessionRepository;
}

@Override
@Transactional(readOnly = true)
public List<GameSessionDto> findAll() {
    return gamesessionRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GameSessionDto> findById(Long id) {
        return gamesessionRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public GameSessionDto save(GameSessionDto gamesessionDto) {
        GameSession gamesession = convertToEntity(gamesessionDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (gamesessionDto.getId() == null) {
        gamesession.setId(null); // Ensure it's null for auto-generation
        }
        gamesession = gamesessionRepository.save(gamesession);
        return convertToDto(gamesession);
        }

        @Override
        public Optional<GameSessionDto> update(Long id, GameSessionDto gamesessionDto) {
            return gamesessionRepository.findById(id)
            .map(existingGameSession -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (gamesessionDto.getStartTime() != null) { // Simple null check
                    existingGameSession.setStartTime(gamesessionDto.getStartTime());
                    }
                    if (gamesessionDto.getEndTime() != null) { // Simple null check
                    existingGameSession.setEndTime(gamesessionDto.getEndTime());
                    }
                    if (gamesessionDto.getFinalScore() != null) { // Simple null check
                    existingGameSession.setFinalScore(gamesessionDto.getFinalScore());
                    }
            GameSession updatedGameSession = gamesessionRepository.save(existingGameSession);
            return convertToDto(updatedGameSession);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (gamesessionRepository.existsById(id)) {
            gamesessionRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private GameSessionDto convertToDto(GameSession gamesession) {
            GameSessionDto dto = new GameSessionDto();
            BeanUtils.copyProperties(gamesession, dto);
            return dto;
            }

            private GameSession convertToEntity(GameSessionDto gamesessionDto) {
            GameSession entity = new GameSession();
            BeanUtils.copyProperties(gamesessionDto, entity);
            return entity;
            }
            }