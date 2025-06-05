package com.example.demo.service.impl;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // Good practice for service layer methods modifying data
public class BookServiceImpl implements BookService {

private final BookRepository bookRepository;

public BookServiceImpl(BookRepository bookRepository) {
this.bookRepository = bookRepository;
}

@Override
@Transactional(readOnly = true)
public List<BookDto> findAll() {
    return bookRepository.findAll().stream()
    .map(this::convertToDto)
    .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDto> findById(Long id) {
        return bookRepository.findById(id)
        .map(this::convertToDto);
        }

        @Override
        public BookDto save(BookDto bookDto) {
        Book book = convertToEntity(bookDto);
        // For new entities, ID might be null or 0.
        // Ensure ID is not set if it's meant to be generated.
        if (bookDto.getId() == null) {
        book.setId(null); // Ensure it's null for auto-generation
        }
        book = bookRepository.save(book);
        return convertToDto(book);
        }

        @Override
        public Optional<BookDto> update(Long id, BookDto bookDto) {
            return bookRepository.findById(id)
            .map(existingBook -> {
            // Update fields from DTO. Be careful with BeanUtils if non-null properties are important
            // Manually map fields for more control:
                    if (bookDto.getTitle() != null) { // Simple null check
                    existingBook.setTitle(bookDto.getTitle());
                    }
                    if (bookDto.getPrice() != null) { // Simple null check
                    existingBook.setPrice(bookDto.getPrice());
                    }
                    if (bookDto.getAuthor() != null) { // Simple null check
                    existingBook.setAuthor(bookDto.getAuthor());
                    }
            Book updatedBook = bookRepository.save(existingBook);
            return convertToDto(updatedBook);
            });
            }


            @Override
            public boolean deleteById(Long id) {
            if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            return true;
            }
            return false;
            }

            // --- Helper Methods for DTO/Entity Conversion ---
            private BookDto convertToDto(Book book) {
            BookDto dto = new BookDto();
            BeanUtils.copyProperties(book, dto);
            return dto;
            }

            private Book convertToEntity(BookDto bookDto) {
            Book entity = new Book();
            BeanUtils.copyProperties(bookDto, entity);
            return entity;
            }
            }