package com.example.demo.service;

import com.example.demo.dto.AuthorDto;
import java.util.List;
import java.util.Optional;

public interface AuthorService {
List<AuthorDto> findAll();
    Optional<AuthorDto> findById(Long id);
        AuthorDto save(AuthorDto authorDto);
        Optional<AuthorDto> update(Long id, AuthorDto authorDto);
            boolean deleteById(Long id);
            }