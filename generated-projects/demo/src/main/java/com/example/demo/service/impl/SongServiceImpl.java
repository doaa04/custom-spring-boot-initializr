package com.example.demo.service.impl;

import com.example.demo.dto.SongDto;
import com.example.demo.entity.Song;
import com.example.demo.repository.SongRepository;
import com.example.demo.service.SongService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class SongServiceImpl implements SongService {

private final SongRepository songRepository;

public SongServiceImpl(SongRepository songRepository) {
this.songRepository = songRepository;
}

@Override
@Transactional(readOnly = true)
public List<SongDto> findAll() {
    return songRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SongDto> findById(Long id) {
        return songRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public SongDto save(SongDto songDto) {
        Song song = convertToEntity(songDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (songDto.getId() == null) {
        song.setId(null); // Ensure it's null for auto-generation
        }
        song = songRepository.save(song);
        return convertToDto(song);
        }

        @Override
        public Optional<SongDto> update(Long id, SongDto songDto) {
            return songRepository.findById(id)
            .map(existingSong -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (songDto.getTitle() != null) { // Simple null check
                    existingSong.setTitle(songDto.getTitle());
                    }
                    if (songDto.getArtist() != null) { // Simple null check
                    existingSong.setArtist(songDto.getArtist());
                    }
                    if (songDto.getAlbum() != null) { // Simple null check
                    existingSong.setAlbum(songDto.getAlbum());
                    }
                    if (songDto.getDuration() != null) { // Simple null check
                    existingSong.setDuration(songDto.getDuration());
                    }
                    if (songDto.getTrackNumber() != null) { // Simple null check
                    existingSong.setTrackNumber(songDto.getTrackNumber());
                    }
                    if (songDto.getReleaseYear() != null) { // Simple null check
                    existingSong.setReleaseYear(songDto.getReleaseYear());
                    }
            Song updatedSong = songRepository.save(existingSong);
            return convertToDto(updatedSong);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (songRepository.existsById(id)) {
            songRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private SongDto convertToDto(Song song) {
            SongDto dto = new SongDto();
            BeanUtils.copyProperties(song, dto);
            return dto;
            }

            private Song convertToEntity(SongDto songDto) {
            Song entity = new Song();
            BeanUtils.copyProperties(songDto, entity);
            return entity;
            }
            }