package com.example.demo.service;

import com.example.demo.dto.BookDto;
import java.util.List;
import java.util.Optional;

public interface BookService {
List<BookDto> findAll();
    Optional<BookDto> findById(Long id);
        BookDto save(BookDto bookDto);
        Optional<BookDto> update(Long id, BookDto bookDto);
            boolean deleteById(Long id);
            }