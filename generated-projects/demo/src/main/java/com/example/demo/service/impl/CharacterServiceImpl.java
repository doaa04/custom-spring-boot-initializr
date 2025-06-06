package com.example.demo.service.impl;

import com.example.demo.dto.CharacterDto;
import com.example.demo.entity.Character;
import com.example.demo.repository.CharacterRepository;
import com.example.demo.service.CharacterService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class CharacterServiceImpl implements CharacterService {

private final CharacterRepository characterRepository;

public CharacterServiceImpl(CharacterRepository characterRepository) {
this.characterRepository = characterRepository;
}

@Override
@Transactional(readOnly = true)
public List<CharacterDto> findAll() {
    return characterRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CharacterDto> findById(Long id) {
        return characterRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public CharacterDto save(CharacterDto characterDto) {
        Character character = convertToEntity(characterDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (characterDto.getId() == null) {
        character.setId(null); // Ensure it's null for auto-generation
        }
        character = characterRepository.save(character);
        return convertToDto(character);
        }

        @Override
        public Optional<CharacterDto> update(Long id, CharacterDto characterDto) {
            return characterRepository.findById(id)
            .map(existingCharacter -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (characterDto.getName() != null) { // Simple null check
                    existingCharacter.setName(characterDto.getName());
                    }
                    if (characterDto.getScore() != null) { // Simple null check
                    existingCharacter.setScore(characterDto.getScore());
                    }
                    if (characterDto.getIsAlive() != null) { // Simple null check
                    existingCharacter.setIsAlive(characterDto.getIsAlive());
                    }
            Character updatedCharacter = characterRepository.save(existingCharacter);
            return convertToDto(updatedCharacter);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (characterRepository.existsById(id)) {
            characterRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private CharacterDto convertToDto(Character character) {
            CharacterDto dto = new CharacterDto();
            BeanUtils.copyProperties(character, dto);
            return dto;
            }

            private Character convertToEntity(CharacterDto characterDto) {
            Character entity = new Character();
            BeanUtils.copyProperties(characterDto, entity);
            return entity;
            }
            }