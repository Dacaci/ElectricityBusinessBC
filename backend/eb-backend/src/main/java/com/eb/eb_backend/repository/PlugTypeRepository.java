package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.PlugType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlugTypeRepository extends JpaRepository<PlugType, Long> {
    Optional<PlugType> findByName(String name);
}





























