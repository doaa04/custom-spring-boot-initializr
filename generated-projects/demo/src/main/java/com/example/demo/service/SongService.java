package com.example.demo.service;

import com.example.demo.dto.SongDto;
import java.util.List;
import java.util.Optional;

public interface SongService {
List<SongDto> findAll();
    Optional<SongDto> findById(Long id);
        SongDto save(SongDto songDto);
        Optional<SongDto> update(Long id, SongDto songDto);
            boolean deleteById(Long id);
            }