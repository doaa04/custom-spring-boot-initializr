package com.example.demo.service;

import com.example.demo.dto.CharacterDto;
import java.util.List;
import java.util.Optional;

public interface CharacterService {
List<CharacterDto> findAll();
    Optional<CharacterDto> findById(Long id);
        CharacterDto save(CharacterDto characterDto);
        Optional<CharacterDto> update(Long id, CharacterDto characterDto);
            boolean deleteById(Long id);
            }