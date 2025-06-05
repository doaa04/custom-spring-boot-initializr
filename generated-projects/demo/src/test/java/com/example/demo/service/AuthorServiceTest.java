package com.example.demo.service;

import com.example.demo.dto.AuthorDto;
import com.example.demo.entity.Author;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.service.impl.AuthorServiceImpl;
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
@DisplayName("Author Service Unit Tests")
class AuthorServiceTest {

@Mock
private AuthorRepository authorRepository;

@InjectMocks
private AuthorServiceImpl authorService;

private Author testAuthor;
private AuthorDto testAuthorDto;

@BeforeEach
void setUp() {
testAuthor = new Author();
testAuthor.setId(1L);
            testAuthor.setFirstName("testFirstName");
            testAuthor.setLastName("testLastName");

testAuthorDto = new AuthorDto();
testAuthorDto.setId(1L);
            testAuthorDto.setFirstName("testFirstName");
            testAuthorDto.setLastName("testLastName");
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Author> entities = Arrays.asList(testAuthor);
when(authorRepository.findAll()).thenReturn(entities);

// When
List<AuthorDto> result = authorService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testAuthor.getId());
    verify(authorRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(authorRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<AuthorDto> result = authorService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(authorRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(authorRepository.findById(anyLong())).thenReturn(Optional.of(testAuthor));

        // When
        Optional<AuthorDto> result = authorService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(authorRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<AuthorDto> result = authorService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(authorRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);

                // When
                AuthorDto result = authorService.save(testAuthorDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testAuthor.getId());
                verify(authorRepository).save(any(Author.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(authorRepository.findById(anyLong())).thenReturn(Optional.of(testAuthor));
                when(authorRepository.save(any(Author.class))).thenReturn(testAuthor);

                // When
                Optional<AuthorDto> result = authorService.update(1L, testAuthorDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(authorRepository).findById(1L);
                    verify(authorRepository).save(any(Author.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(authorRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<AuthorDto> result = authorService.update(999L, testAuthorDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(authorRepository).findById(999L);
                        verify(authorRepository, never()).save(any(Author.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(authorRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = authorService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(authorRepository).existsById(1L);
                        verify(authorRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(authorRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = authorService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(authorRepository).existsById(999L);
                        verify(authorRepository, never()).deleteById(anyLong());
                        }
                        }