package com.example.demo.repository;

import com.example.demo.entity.Obstacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObstacleRepository extends JpaRepository<Obstacle, Long> {
// Add custom query methods if needed
}