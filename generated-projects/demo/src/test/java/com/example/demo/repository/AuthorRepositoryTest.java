package com.example.demo.repository;

import com.example.demo.entity.Author;
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
@DisplayName("Author Repository Tests")
class AuthorRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private AuthorRepository authorRepository;

private Author testAuthor;

@BeforeEach
void setUp() {
testAuthor = new Author();
            testAuthor.setFirstName("testFirstName");
            testAuthor.setLastName("testLastName");
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Author savedEntity = entityManager.persistAndFlush(testAuthor);

// When
Optional<Author> result = authorRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getFirstName()).isEqualTo(testAuthor.getFirstName());
        assertThat(result.get().getLastName()).isEqualTo(testAuthor.getLastName());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Author> result = authorRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Author savedEntity = authorRepository.save(testAuthor);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getFirstName()).isEqualTo(testAuthor.getFirstName());
        assertThat(savedEntity.getLastName()).isEqualTo(testAuthor.getLastName());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Author savedEntity = entityManager.persistAndFlush(testAuthor);
entityManager.detach(savedEntity);

            savedEntity.setFirstName("updatedFirstName");
            savedEntity.setLastName("updatedLastName");

// When
Author updatedEntity = authorRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getFirstName()).isEqualTo(savedEntity.getFirstName());
        assertThat(updatedEntity.getLastName()).isEqualTo(savedEntity.getLastName());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Author savedEntity = entityManager.persistAndFlush(testAuthor);

// When
authorRepository.deleteById(savedEntity.getId());

// Then
Optional<Author> result = authorRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Author entity1 = entityManager.persistAndFlush(testAuthor);

Author entity2 = new Author();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
entityManager.persistAndFlush(entity2);

// When
List<Author> entities = authorRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Author entity = new Author();
            entity.setFirstName("testFirstName" + i);
            entity.setLastName("testLastName" + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Author> page = authorRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Author savedEntity = entityManager.persistAndFlush(testAuthor);

// When
boolean exists = authorRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = authorRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testAuthor);

Author entity2 = new Author();
            entity2.setFirstName("anotherFirstName");
            entity2.setLastName("anotherLastName");
entityManager.persistAndFlush(entity2);

// When
long count = authorRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}