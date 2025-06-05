package com.example.demo.service;

import com.example.demo.dto.RoomDto;
import java.util.List;
import java.util.Optional;

public interface RoomService {
List<RoomDto> findAll();
    Optional<RoomDto> findById(Long id);
        RoomDto save(RoomDto roomDto);
        Optional<RoomDto> update(Long id, RoomDto roomDto);
            boolean deleteById(Long id);
            }