package com.example.demo.controller;

import com.example.demo.dto.RoomDto;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/rooms") // Simple pluralization
public class RoomController {

private final RoomService roomService;

public RoomController(RoomService roomService) {
this.roomService = roomService;
}

@GetMapping
public ResponseEntity<List<RoomDto>> getAllRooms() {
    return ResponseEntity.ok(roomService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long id) {
        return roomService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody RoomDto roomDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (roomDto.getId() != null) {
            roomDto.setId(null);
            }
            RoomDto savedDto = roomService.save(roomDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<RoomDto> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDto roomDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (roomDto.getId() == null) {
                roomDto.setId(id);
                } else if (!roomDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return roomService.update(id, roomDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
                    if (roomService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found with id " + id);
                    }
                    }
                    }