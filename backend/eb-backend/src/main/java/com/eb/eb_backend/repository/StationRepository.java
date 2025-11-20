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
    
    // Recherche par propriétaire via location.owner
    @Query("SELECT s FROM Station s WHERE s.location.owner = :owner")
    Page<Station> findByOwner(@Param("owner") User owner, Pageable pageable);
    
    // Stations actives d'un propriétaire
    @Query("SELECT s FROM Station s WHERE s.location.owner = :owner AND s.isActive = true")
    List<Station> findByOwnerAndIsActiveTrue(@Param("owner") User owner);
    
    List<Station> findByLocationAndIsActiveTrue(Location location);
    
    List<Station> findByIsActiveTrue();
    
    @Query("SELECT s FROM Station s WHERE s.isActive = true")
    Page<Station> findBySearchQuery(@Param("q") String query, Pageable pageable);
    
    @Query("SELECT s FROM Station s WHERE " +
           "s.isActive = true AND " +
           "s.location.latitude BETWEEN :minLat AND :maxLat AND " +
           "s.location.longitude BETWEEN :minLng AND :maxLng")
    List<Station> findByCoordinatesRange(@Param("minLat") java.math.BigDecimal minLat,
                                       @Param("maxLat") java.math.BigDecimal maxLat,
                                       @Param("minLng") java.math.BigDecimal minLng,
                                       @Param("maxLng") java.math.BigDecimal maxLng);
    
    // Vérification d'unicité du nom pour un propriétaire
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Station s " +
           "WHERE s.location.owner = :owner AND s.name = :name")
    boolean existsByOwnerAndName(@Param("owner") User owner, @Param("name") String name);
}
