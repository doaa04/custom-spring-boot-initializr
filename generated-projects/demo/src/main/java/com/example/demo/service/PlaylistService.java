package com.example.demo.service;

import com.example.demo.dto.PlaylistDto;
import java.util.List;
import java.util.Optional;

public interface PlaylistService {
List<PlaylistDto> findAll();
    Optional<PlaylistDto> findById(Long id);
        PlaylistDto save(PlaylistDto playlistDto);
        Optional<PlaylistDto> update(Long id, PlaylistDto playlistDto);
            boolean deleteById(Long id);
            }