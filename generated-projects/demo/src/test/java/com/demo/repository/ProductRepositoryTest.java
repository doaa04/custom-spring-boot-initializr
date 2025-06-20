package com.demo.repository;

import com.demo.entity.Product;
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
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

@Autowired
private TestEntityManager entityManager;

@Autowired
private ProductRepository productRepository;

private Product testProduct;

@BeforeEach
void setUp() {
testProduct = new Product();
            testProduct.setName("testName");
            testProduct.setPrice(100);
}

@Test
@DisplayName("Should find entity by ID when entity exists")
void findById_ExistingEntity_ReturnsEntity() {
// Given
Product savedEntity = entityManager.persistAndFlush(testProduct);

// When
Optional<Product> result = productRepository.findById(savedEntity.getId());

// Then
assertThat(result).isPresent();
assertThat(result.get().getId()).isEqualTo(savedEntity.getId());
        assertThat(result.get().getName()).isEqualTo(testProduct.getName());
        assertThat(result.get().getPrice()).isEqualTo(testProduct.getPrice());
}

@Test
@DisplayName("Should return empty when finding by non-existing ID")
void findById_NonExistingEntity_ReturnsEmpty() {
// When
Optional<Product> result = productRepository.findById(999L);

// Then
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should save entity successfully")
void save_ValidEntity_ReturnsSavedEntity() {
// When
Product savedEntity = productRepository.save(testProduct);

// Then
assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getName()).isEqualTo(testProduct.getName());
        assertThat(savedEntity.getPrice()).isEqualTo(testProduct.getPrice());
}

@Test
@DisplayName("Should update entity successfully")
void save_UpdateExistingEntity_ReturnsUpdatedEntity() {
// Given
Product savedEntity = entityManager.persistAndFlush(testProduct);
entityManager.detach(savedEntity);

            savedEntity.setName("updatedName");
            savedEntity.setPrice(200);

// When
Product updatedEntity = productRepository.save(savedEntity);

// Then
assertThat(updatedEntity.getId()).isEqualTo(savedEntity.getId());
        assertThat(updatedEntity.getName()).isEqualTo(savedEntity.getName());
        assertThat(updatedEntity.getPrice()).isEqualTo(savedEntity.getPrice());
}

@Test
@DisplayName("Should delete entity successfully")
void deleteById_ExistingEntity_DeletesEntity() {
// Given
Product savedEntity = entityManager.persistAndFlush(testProduct);

// When
productRepository.deleteById(savedEntity.getId());

// Then
Optional<Product> result = productRepository.findById(savedEntity.getId());
assertThat(result).isEmpty();
}

@Test
@DisplayName("Should return all entities")
void findAll_ReturnsAllEntities() {
// Given
Product entity1 = entityManager.persistAndFlush(testProduct);

Product entity2 = new Product();
            entity2.setName("anotherName");
            entity2.setPrice(200);
entityManager.persistAndFlush(entity2);

// When
List<Product> entities = productRepository.findAll();

// Then
assertThat(entities).hasSize(2);
assertThat(entities).extracting("id").contains(entity1.getId(), entity2.getId());
}

@Test
@DisplayName("Should support pagination")
void findAll_WithPageable_ReturnsPagedResult() {
// Given
for (int i = 0; i < 5; i++) {
Product entity = new Product();
            entity.setName("testName" + i);
            entity.setPrice(100 + i);
entityManager.persistAndFlush(entity);
}

// When
Pageable pageable = PageRequest.of(0, 3);
Page<Product> page = productRepository.findAll(pageable);

// Then
assertThat(page.getContent()).hasSize(3);
assertThat(page.getTotalElements()).isEqualTo(5);
assertThat(page.getTotalPages()).isEqualTo(2);
}

@Test
@DisplayName("Should check if entity exists by ID")
void existsById_ExistingEntity_ReturnsTrue() {
// Given
Product savedEntity = entityManager.persistAndFlush(testProduct);

// When
boolean exists = productRepository.existsById(savedEntity.getId());

// Then
assertThat(exists).isTrue();
}

@Test
@DisplayName("Should return false when entity does not exist")
void existsById_NonExistingEntity_ReturnsFalse() {
// When
boolean exists = productRepository.existsById(999L);

// Then
assertThat(exists).isFalse();
}

@Test
@DisplayName("Should return correct count of entities")
void count_ReturnsCorrectCount() {
// Given
entityManager.persistAndFlush(testProduct);

Product entity2 = new Product();
            entity2.setName("anotherName");
            entity2.setPrice(200);
entityManager.persistAndFlush(entity2);

// When
long count = productRepository.count();

// Then
assertThat(count).isEqualTo(2);
}
}