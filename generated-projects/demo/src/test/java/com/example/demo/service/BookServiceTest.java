package com.example.demo.service;

import com.example.demo.dto.BookDto;
import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Book Service Unit Tests")
class BookServiceTest {

@Mock
private BookRepository bookRepository;

@InjectMocks
private BookServiceImpl bookService;

private Book testBook;
private BookDto testBookDto;

@BeforeEach
void setUp() {
testBook = new Book();
testBook.setId(1L);
            testBook.setTitle("testTitle");
            testBook.setPrice(100.0);

testBookDto = new BookDto();
testBookDto.setId(1L);
            testBookDto.setTitle("testTitle");
            testBookDto.setPrice(100.0);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Book> entities = Arrays.asList(testBook);
when(bookRepository.findAll()).thenReturn(entities);

// When
List<BookDto> result = bookService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testBook.getId());
    verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(bookRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<BookDto> result = bookService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(testBook));

        // When
        Optional<BookDto> result = bookService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(bookRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<BookDto> result = bookService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(bookRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(bookRepository.save(any(Book.class))).thenReturn(testBook);

                // When
                BookDto result = bookService.save(testBookDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testBook.getId());
                verify(bookRepository).save(any(Book.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(bookRepository.findById(anyLong())).thenReturn(Optional.of(testBook));
                when(bookRepository.save(any(Book.class))).thenReturn(testBook);

                // When
                Optional<BookDto> result = bookService.update(1L, testBookDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(bookRepository).findById(1L);
                    verify(bookRepository).save(any(Book.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<BookDto> result = bookService.update(999L, testBookDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(bookRepository).findById(999L);
                        verify(bookRepository, never()).save(any(Book.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(bookRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = bookService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(bookRepository).existsById(1L);
                        verify(bookRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(bookRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = bookService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(bookRepository).existsById(999L);
                        verify(bookRepository, never()).deleteById(anyLong());
                        }
                        }