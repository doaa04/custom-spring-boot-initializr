package com.example.demo.repository;

import com.example.demo.entity.Book;
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
@DisplayName("Book Repository Tests")
class BookRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private BookRepository bookRepository;

private Book testBook;

@BeforeEach
void setUp() {
testBook = new Book();
            testBook.setTitle("testTitle");
            testBook.setIsbn("testIsbn");
            testBook.setPublicationDate(java.time.LocalDate.now());
            testBook.setPrice(100.0);
            testBook.setAuthorId(100L);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Book savedEntity = entityManager.persistAndFlush(testBook);

// When
Optional<Book> result = bookRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getTitle()).isEqualTo(testBook.getTitle());
        assertThat(result.get().getIsbn()).isEqualTo(testBook.getIsbn());
        assertThat(result.get().getPublicationDate()).isEqualTo(testBook.getPublicationDate());
        assertThat(result.get().getPrice()).isEqualTo(testBook.getPrice());
        assertThat(result.get().getAuthorId()).isEqualTo(testBook.getAuthorId());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Book> result = bookRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Book savedEntity = bookRepository.save(testBook);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getTitle()).isEqualTo(testBook.getTitle());
        assertThat(savedEntity.getIsbn()).isEqualTo(testBook.getIsbn());
        assertThat(savedEntity.getPublicationDate()).isEqualTo(testBook.getPublicationDate());
        assertThat(savedEntity.getPrice()).isEqualTo(testBook.getPrice());
        assertThat(savedEntity.getAuthorId()).isEqualTo(testBook.getAuthorId());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Book savedEntity = entityManager.persistAndFlush(testBook);
entityManager.detach(savedEntity);

            savedEntity.setTitle("updatedTitle");
            savedEntity.setIsbn("updatedIsbn");
            savedEntity.setPublicationDate(java.time.LocalDate.now().plusDays(1));
            savedEntity.setPrice(200.0);
            savedEntity.setAuthorId(200L);

// When
Book updatedEntity = bookRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getTitle()).isEqualTo(savedEntity.getTitle());
        assertThat(updatedEntity.getIsbn()).isEqualTo(savedEntity.getIsbn());
        assertThat(updatedEntity.getPublicationDate()).isEqualTo(savedEntity.getPublicationDate());
        assertThat(updatedEntity.getPrice()).isEqualTo(savedEntity.getPrice());
        assertThat(updatedEntity.getAuthorId()).isEqualTo(savedEntity.getAuthorId());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Book savedEntity = entityManager.persistAndFlush(testBook);

// When
bookRepository.deleteById(savedEntity.getId());

// Then
Optional<Book> result = bookRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Book entity1 = entityManager.persistAndFlush(testBook);

Book entity2 = new Book();
            entity2.setTitle("anotherTitle");
            entity2.setIsbn("anotherIsbn");
            entity2.setPublicationDate(java.time.LocalDate.now().plusDays(1));
            entity2.setPrice(200.0);
            entity2.setAuthorId(200L);
entityManager.persistAndFlush(entity2);

// When
List<Book> entities = bookRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Book entity = new Book();
            entity.setTitle("testTitle" + i);
            entity.setIsbn("testIsbn" + i);
            entity.setPublicationDate(java.time.LocalDate.now().plusDays(i));
            entity.setPrice(100.0 + i);
            entity.setAuthorId(100L + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Book> page = bookRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Book savedEntity = entityManager.persistAndFlush(testBook);

// When
boolean exists = bookRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = bookRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testBook);

Book entity2 = new Book();
            entity2.setTitle("anotherTitle");
            entity2.setIsbn("anotherIsbn");
            entity2.setPublicationDate(java.time.LocalDate.now().plusDays(1));
            entity2.setPrice(200.0);
            entity2.setAuthorId(200L);
entityManager.persistAndFlush(entity2);

// When
long count = bookRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}