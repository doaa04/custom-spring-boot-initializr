package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Room Repository Tests")
class RoomRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private RoomRepository roomRepository;

private Room testRoom;

@BeforeEach
void setUp() {
testRoom = new Room();
            testRoom.setRoomNumber("testRoomNumber");
            testRoom.setType("testType");
            testRoom.setIsAvailable(true);
            testRoom.setDepartmentId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Room savedEntity = entityManager.persistAndFlush(testRoom);

// When
Optional<Room> result = roomRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getRoomNumber()).isEqualTo(testRoom.getRoomNumber());
        assertThat(result.get().getType()).isEqualTo(testRoom.getType());
        assertThat(result.get().getIsAvailable()).isEqualTo(testRoom.getIsAvailable());
        assertThat(result.get().getDepartmentId()).isEqualTo(testRoom.getDepartmentId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Room> result = roomRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Room savedEntity = roomRepository.save(testRoom);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getRoomNumber()).isEqualTo(testRoom.getRoomNumber());
        assertThat(savedEntity.getType()).isEqualTo(testRoom.getType());
        assertThat(savedEntity.getIsAvailable()).isEqualTo(testRoom.getIsAvailable());
        assertThat(savedEntity.getDepartmentId()).isEqualTo(testRoom.getDepartmentId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Room savedEntity = entityManager.persistAndFlush(testRoom);
entityManager.detach(savedEntity);

            savedEntity.setRoomNumber("updatedRoomNumber");
            savedEntity.setType("updatedType");
            savedEntity.setIsAvailable(false);
            savedEntity.setDepartmentId(200L);

// When
Room updatedEntity = roomRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getRoomNumber()).isEqualTo(savedEntity.getRoomNumber());
        assertThat(updatedEntity.getType()).isEqualTo(savedEntity.getType());
        assertThat(updatedEntity.getIsAvailable()).isEqualTo(savedEntity.getIsAvailable());
        assertThat(updatedEntity.getDepartmentId()).isEqualTo(savedEntity.getDepartmentId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Room savedEntity = entityManager.persistAndFlush(testRoom);

// When
roomRepository.deleteById(savedEntity.getId());

// Then
Optional<Room> result = roomRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Room entity1 = entityManager.persistAndFlush(testRoom);

Room entity2 = new Room();
            entity2.setRoomNumber("anotherRoomNumber");
            entity2.setType("anotherType");
            entity2.setIsAvailable(false);
            entity2.setDepartmentId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Room> entities = roomRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Room entity = new Room();
            entity.setRoomNumber("testRoomNumber" + i);
            entity.setType("testType" + i);
            entity.setIsAvailable(i % 2 == 0);
            entity.setDepartmentId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Room> page = roomRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Room savedEntity = entityManager.persistAndFlush(testRoom);

// When
boolean exists = roomRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = roomRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testRoom);

Room entity2 = new Room();
            entity2.setRoomNumber("anotherRoomNumber");
            entity2.setType("anotherType");
            entity2.setIsAvailable(false);
            entity2.setDepartmentId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = roomRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}