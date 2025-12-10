package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByStationId(Long stationId);
    List<Media> findByLocationId(Long locationId);
    List<Media> findByUserId(Long userId);
}
































