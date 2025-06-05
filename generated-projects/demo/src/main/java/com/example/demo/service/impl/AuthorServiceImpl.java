package com.example.demo.service.impl;

import com.example.demo.dto.AuthorDto;
import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.service.AuthorService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class AuthorServiceImpl implements AuthorService {

private final AuthorRepository authorRepository;

public AuthorServiceImpl(AuthorRepository authorRepository) {
this.authorRepository = authorRepository;
}

@Override
@Transactional(readOnly = true)
public List<AuthorDto> findAll() {
    return authorRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthorDto> findById(Long id) {
        return authorRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public AuthorDto save(AuthorDto authorDto) {
        Author author = convertToEntity(authorDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (authorDto.getId() == null) {
        author.setId(null); // Ensure it's null for auto-generation
        }
        author = authorRepository.save(author);
        return convertToDto(author);
        }

        @Override
        public Optional<AuthorDto> update(Long id, AuthorDto authorDto) {
            return authorRepository.findById(id)
            .map(existingAuthor -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (authorDto.getFirstName() != null) { // Simple null check
                    existingAuthor.setFirstName(authorDto.getFirstName());
                    }
                    if (authorDto.getLastName() != null) { // Simple null check
                    existingAuthor.setLastName(authorDto.getLastName());
                    }
            Author updatedAuthor = authorRepository.save(existingAuthor);
            return convertToDto(updatedAuthor);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private AuthorDto convertToDto(Author author) {
            AuthorDto dto = new AuthorDto();
            BeanUtils.copyProperties(author, dto);
            return dto;
            }

            private Author convertToEntity(AuthorDto authorDto) {
            Author entity = new Author();
            BeanUtils.copyProperties(authorDto, entity);
            return entity;
            }
            }