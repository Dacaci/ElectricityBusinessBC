package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.Station;
import com.eb.eb_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    
    Page<Station> findByOwner(User owner, Pageable pageable);
    
    List<Station> findByOwnerAndIsActiveTrue(User owner);
    
    List<Station> findByLocationAndIsActiveTrue(Location location);
    
    @Query("SELECT s FROM Station s WHERE " +
           "s.isActive = true AND " +
           "(:q IS NULL OR " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
           "LOWER(s.plugType) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Station> findBySearchQuery(@Param("q") String query, Pageable pageable);
    
    @Query("SELECT s FROM Station s WHERE " +
           "s.isActive = true AND " +
           "s.location.latitude BETWEEN :minLat AND :maxLat AND " +
           "s.location.longitude BETWEEN :minLng AND :maxLng")
    List<Station> findByCoordinatesRange(@Param("minLat") java.math.BigDecimal minLat,
                                       @Param("maxLat") java.math.BigDecimal maxLat,
                                       @Param("minLng") java.math.BigDecimal minLng,
                                       @Param("maxLng") java.math.BigDecimal maxLng);
    
    boolean existsByOwnerAndName(User owner, String name);
}
