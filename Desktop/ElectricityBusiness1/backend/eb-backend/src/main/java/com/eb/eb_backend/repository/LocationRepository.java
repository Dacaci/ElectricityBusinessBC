package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.Location;
import com.eb.eb_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Page<Location> findByOwner(User owner, Pageable pageable);
    
    List<Location> findByOwnerAndIsActiveTrue(User owner);
    
    @Query("SELECT l FROM Location l WHERE l.isActive = true")
    Page<Location> findBySearchQuery(@Param("q") String query, Pageable pageable);
    
    @Query("SELECT l FROM Location l WHERE " +
           "l.isActive = true AND " +
           "l.latitude BETWEEN :minLat AND :maxLat AND " +
           "l.longitude BETWEEN :minLng AND :maxLng")
    List<Location> findByCoordinatesRange(@Param("minLat") BigDecimal minLat,
                                        @Param("maxLat") BigDecimal maxLat,
                                        @Param("minLng") BigDecimal minLng,
                                        @Param("maxLng") BigDecimal maxLng);
}
