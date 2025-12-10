package com.eb.eb_backend.repository;

import com.eb.eb_backend.entity.User;
import com.eb.eb_backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);
    List<Vehicle> findByUsers(User user);
}
































