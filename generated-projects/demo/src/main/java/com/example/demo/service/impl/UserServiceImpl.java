package com.example.demo.service.impl;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class UserServiceImpl implements UserService {

private final UserRepository userRepository;

public UserServiceImpl(UserRepository userRepository) {
this.userRepository = userRepository;
}

@Override
@Transactional(readOnly = true)
public List<UserDto> findAll() {
    return userRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public UserDto save(UserDto userDto) {
        User user = convertToEntity(userDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (userDto.getId() == null) {
        user.setId(null); // Ensure it's null for auto-generation
        }
        user = userRepository.save(user);
        return convertToDto(user);
        }

        @Override
        public Optional<UserDto> update(Long id, UserDto userDto) {
            return userRepository.findById(id)
            .map(existingUser -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (userDto.getUsername() != null) { // Simple null check
                    existingUser.setUsername(userDto.getUsername());
                    }
                    if (userDto.getEmail() != null) { // Simple null check
                    existingUser.setEmail(userDto.getEmail());
                    }
                    if (userDto.getPasswordHash() != null) { // Simple null check
                    existingUser.setPasswordHash(userDto.getPasswordHash());
                    }
            User updatedUser = userRepository.save(existingUser);
            return convertToDto(updatedUser);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private UserDto convertToDto(User user) {
            UserDto dto = new UserDto();
            BeanUtils.copyProperties(user, dto);
            return dto;
            }

            private User convertToEntity(UserDto userDto) {
            User entity = new User();
            BeanUtils.copyProperties(userDto, entity);
            return entity;
            }
            }