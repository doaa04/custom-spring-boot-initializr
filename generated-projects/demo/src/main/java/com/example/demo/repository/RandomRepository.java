package com.example.demo.repository;

import com.example.demo.entity.Random;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomRepository extends JpaRepository<Random, Long> {
// Add custom query methods if needed
}