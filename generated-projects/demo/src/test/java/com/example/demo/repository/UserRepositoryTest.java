package com.example.demo.repository;

import com.example.demo.entity.User;
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
@DisplayName("User Repository Tests")
class UserRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private UserRepository userRepository;

private User testUser;

@BeforeEach
void setUp() {
testUser = new User();
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
User savedEntity = entityManager.persistAndFlush(testUser);

// When
Optional<User> result = userRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<User> result = userRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
User savedEntity = userRepository.save(testUser);

// Then
assertThat(savedEntity.getId()).isNotNull();
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
User savedEntity = entityManager.persistAndFlush(testUser);
entityManager.detach(savedEntity);


// When
User updatedEntity = userRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
User savedEntity = entityManager.persistAndFlush(testUser);

// When
userRepository.deleteById(savedEntity.getId());

// Then
Optional<User> result = userRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
User entity1 = entityManager.persistAndFlush(testUser);

User entity2 = new User();
entityManager.persistAndFlush(entity2);

// When
List<User> entities = userRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
User entity = new User();
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<User> page = userRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
User savedEntity = entityManager.persistAndFlush(testUser);

// When
boolean exists = userRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = userRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testUser);

User entity2 = new User();
entityManager.persistAndFlush(entity2);

// When
long count = userRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}