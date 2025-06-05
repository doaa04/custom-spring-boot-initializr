package com.example.demo.service;

import com.example.demo.dto.RoomDto;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import com.example.demo.service.impl.RoomServiceImpl;
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
@DisplayName("Room Service Unit Tests")
class RoomServiceTest {

@Mock
private RoomRepository roomRepository;

@InjectMocks
private RoomServiceImpl roomService;

private Room testRoom;
private RoomDto testRoomDto;

@BeforeEach
void setUp() {
testRoom = new Room();
testRoom.setId(1L);
            testRoom.setRoomNumber("testRoomNumber");
            testRoom.setType("testType");
            testRoom.setIsAvailable(true);
            testRoom.setDepartmentId(100L);

testRoomDto = new RoomDto();
testRoomDto.setId(1L);
            testRoomDto.setRoomNumber("testRoomNumber");
            testRoomDto.setType("testType");
            testRoomDto.setIsAvailable(true);
            testRoomDto.setDepartmentId(100L);
}

@Test
@DisplayName("Should return all entities when findAll is called")
void findAll_ReturnsAllEntities() {
// Given
List<Room> entities = Arrays.asList(testRoom);
when(roomRepository.findAll()).thenReturn(entities);

// When
List<RoomDto> result = roomService.findAll();

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getId()).isEqualTo(testRoom.getId());
    verify(roomRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no entities exist")
    void findAll_NoEntities_ReturnsEmptyList() {
    // Given
    when(roomRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<RoomDto> result = roomService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(roomRepository).findAll();
        }

        @Test
        @DisplayName("Should return entity when findById is called with existing ID")
        void findById_ExistingId_ReturnsEntity() {
        // Given
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));

        // When
        Optional<RoomDto> result = roomService.findById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
            verify(roomRepository).findById(1L);
            }

            @Test
            @DisplayName("Should return empty when findById is called with non-existing ID")
            void findById_NonExistingId_ReturnsEmpty() {
            // Given
            when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

            // When
            Optional<RoomDto> result = roomService.findById(999L);

                // Then
                assertThat(result).isEmpty();
                verify(roomRepository).findById(999L);
                }

                @Test
                @DisplayName("Should save entity successfully when valid data is provided")
                void save_ValidEntity_ReturnsSavedEntity() {
                // Given
                when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

                // When
                RoomDto result = roomService.save(testRoomDto);

                // Then
                assertThat(result).isNotNull();
                assertThat(result.getId()).isEqualTo(testRoom.getId());
                verify(roomRepository).save(any(Room.class));
                }

                @Test
                @DisplayName("Should update entity successfully when valid data is provided")
                void update_ValidEntity_ReturnsUpdatedEntity() {
                // Given
                when(roomRepository.findById(anyLong())).thenReturn(Optional.of(testRoom));
                when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

                // When
                Optional<RoomDto> result = roomService.update(1L, testRoomDto);

                    // Then
                    assertThat(result).isPresent();
                    verify(roomRepository).findById(1L);
                    verify(roomRepository).save(any(Room.class));
                    }

                    @Test
                    @DisplayName("Should return empty when updating non-existing entity")
                    void update_NonExistingEntity_ReturnsEmpty() {
                    // Given
                    when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

                    // When
                    Optional<RoomDto> result = roomService.update(999L, testRoomDto);

                        // Then
                        assertThat(result).isEmpty();
                        verify(roomRepository).findById(999L);
                        verify(roomRepository, never()).save(any(Room.class));
                        }

                        @Test
                        @DisplayName("Should delete entity successfully when ID exists")
                        void deleteById_ExistingId_ReturnsTrue() {
                        // Given
                        when(roomRepository.existsById(anyLong())).thenReturn(true);

                        // When
                        boolean result = roomService.deleteById(1L);

                        // Then
                        assertThat(result).isTrue();
                        verify(roomRepository).existsById(1L);
                        verify(roomRepository).deleteById(1L);
                        }

                        @Test
                        @DisplayName("Should return false when deleting non-existing entity")
                        void deleteById_NonExistingId_ReturnsFalse() {
                        // Given
                        when(roomRepository.existsById(anyLong())).thenReturn(false);

                        // When
                        boolean result = roomService.deleteById(999L);

                        // Then
                        assertThat(result).isFalse();
                        verify(roomRepository).existsById(999L);
                        verify(roomRepository, never()).deleteById(anyLong());
                        }
                        }