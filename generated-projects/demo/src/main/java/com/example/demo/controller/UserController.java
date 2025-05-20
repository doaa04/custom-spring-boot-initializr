package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("/api/users") // Simple pluralization
public class UserController {

private final UserService userService;

public UserController(UserService userService) {
this.userService = userService;
}

@GetMapping
public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return userService.findById(id)
        .map(ResponseEntity::ok)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id));
        }

        @PostMapping
        public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
            // Ensure ID is null for creation if it's passed in DTO
            if (userDto.getId() != null) {
            userDto.setId(null);
            }
            UserDto savedDto = userService.save(userDto);
            return new ResponseEntity<>(savedDto, HttpStatus.CREATED);
            }

            @PutMapping("/{id}")
            public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
                // Ensure the DTO's ID matches the path variable ID, or set it.
                if (userDto.getId() == null) {
                userDto.setId(id);
                } else if (!userDto.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID in path and DTO do not match.");
                }

                return userService.update(id, userDto)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id));
                }

                @DeleteMapping("/{id}")
                public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
                    if (userService.deleteById(id)) {
                    return ResponseEntity.noContent().build();
                    } else {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id);
                    }
                    }
                    }