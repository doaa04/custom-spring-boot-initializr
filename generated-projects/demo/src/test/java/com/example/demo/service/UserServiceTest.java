package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.impl.UserServiceImpl;
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
@DisplayName("User Service Unit Tests")
class UserServiceTest {

@Mock
private UserRepository userRepository;

@InjectMocks
private UserServiceImpl userService;

private User testUser;
private UserDto testUserDto;

@BeforeEach
void setUp() {
testUser = new User();
testUser.setId(1L);
            testUser.setUsername("testUsername");
            testUser.setEmail("testEmail");
            testUser.setPasswordHash("testPasswordHash");

testUserDto = new UserDto();
testUserDto.setId(1L);
            testUserDto.setUsername("testUsername");
            testUserDto.setEmail("testEmail");
            testUserDto.setPasswordHash("testPasswordHash");
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<User> entities = Arrays.asList(testUser);
when(userRepository.findAll()).thenReturn(entities);

// When
List<UserDto> result = userService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testUser.getId());
    verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<UserDto> result = userService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        // When
        Optional<UserDto> result = userService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(userRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<UserDto> result = userService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(userRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(userRepository.save(any(User.class))).thenReturn(testUser);

                // When
                UserDto result = userService.save(testUserDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testUser.getId());
                verify(userRepository).save(any(User.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
                when(userRepository.save(any(User.class))).thenReturn(testUser);

                // When
                Optional<UserDto> result = userService.update(1L, testUserDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(userRepository).findById(1L);
                    verify(userRepository).save(any(User.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<UserDto> result = userService.update(999L, testUserDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(userRepository).findById(999L);
                        verify(userRepository, never()).save(any(User.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(userRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = userService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(userRepository).existsById(1L);
                        verify(userRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(userRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = userService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(userRepository).existsById(999L);
                        verify(userRepository, never()).deleteById(anyLong());
                        }
                        }