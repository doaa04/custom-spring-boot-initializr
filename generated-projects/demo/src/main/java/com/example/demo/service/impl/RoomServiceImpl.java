package com.example.demo.service.impl;

import com.example.demo.dto.RoomDto;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.RoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class RoomServiceImpl implements RoomService {

private final RoomRepository roomRepository;

public RoomServiceImpl(RoomRepository roomRepository) {
this.roomRepository = roomRepository;
}

@Override
@Transactional(readOnly = true)
public List<RoomDto> findAll() {
    return roomRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDto> findById(Long id) {
        return roomRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public RoomDto save(RoomDto roomDto) {
        Room room = convertToEntity(roomDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (roomDto.getId() == null) {
        room.setId(null); // Ensure it's null for auto-generation
        }
        room = roomRepository.save(room);
        return convertToDto(room);
        }

        @Override
        public Optional<RoomDto> update(Long id, RoomDto roomDto) {
            return roomRepository.findById(id)
            .map(existingRoom -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (roomDto.getRoomNumber() != null) { // Simple null check
                    existingRoom.setRoomNumber(roomDto.getRoomNumber());
                    }
                    if (roomDto.getType() != null) { // Simple null check
                    existingRoom.setType(roomDto.getType());
                    }
                    if (roomDto.getIsAvailable() != null) { // Simple null check
                    existingRoom.setIsAvailable(roomDto.getIsAvailable());
                    }
                    if (roomDto.getDepartmentId() != null) { // Simple null check
                    existingRoom.setDepartmentId(roomDto.getDepartmentId());
                    }
            Room updatedRoom = roomRepository.save(existingRoom);
            return convertToDto(updatedRoom);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private RoomDto convertToDto(Room room) {
            RoomDto dto = new RoomDto();
            BeanUtils.copyProperties(room, dto);
            return dto;
            }

            private Room convertToEntity(RoomDto roomDto) {
            Room entity = new Room();
            BeanUtils.copyProperties(roomDto, entity);
            return entity;
            }
            }