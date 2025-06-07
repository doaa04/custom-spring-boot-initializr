package com.example.demo.service.impl;

import com.example.demo.dto.PlaylistDto;
import com.example.demo.entity.Playlist;
import com.example.demo.repository.PlaylistRepository;
import com.example.demo.service.PlaylistService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class PlaylistServiceImpl implements PlaylistService {

private final PlaylistRepository playlistRepository;

public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
this.playlistRepository = playlistRepository;
}

@Override
@Transactional(readOnly = true)
public List<PlaylistDto> findAll() {
    return playlistRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlaylistDto> findById(Long id) {
        return playlistRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public PlaylistDto save(PlaylistDto playlistDto) {
        Playlist playlist = convertToEntity(playlistDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (playlistDto.getId() == null) {
        playlist.setId(null); // Ensure it's null for auto-generation
        }
        playlist = playlistRepository.save(playlist);
        return convertToDto(playlist);
        }

        @Override
        public Optional<PlaylistDto> update(Long id, PlaylistDto playlistDto) {
            return playlistRepository.findById(id)
            .map(existingPlaylist -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (playlistDto.getName() != null) { // Simple null check
                    existingPlaylist.setName(playlistDto.getName());
                    }
                    if (playlistDto.getDescription() != null) { // Simple null check
                    existingPlaylist.setDescription(playlistDto.getDescription());
                    }
            Playlist updatedPlaylist = playlistRepository.save(existingPlaylist);
            return convertToDto(updatedPlaylist);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private PlaylistDto convertToDto(Playlist playlist) {
            PlaylistDto dto = new PlaylistDto();
            BeanUtils.copyProperties(playlist, dto);
            return dto;
            }

            private Playlist convertToEntity(PlaylistDto playlistDto) {
            Playlist entity = new Playlist();
            BeanUtils.copyProperties(playlistDto, entity);
            return entity;
            }
            }