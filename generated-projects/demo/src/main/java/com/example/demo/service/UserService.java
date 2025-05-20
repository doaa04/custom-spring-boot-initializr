package com.example.demo.service;

import com.example.demo.dto.UserDto;
import java.util.List;
import java.util.Optional;

public interface UserService {
List<UserDto> findAll();
    Optional<UserDto> findById(Long id);
        UserDto save(UserDto userDto);
        Optional<UserDto> update(Long id, UserDto userDto);
            boolean deleteById(Long id);
            }