package com.citycharge.repository;

import com.citycharge.entity.UserVehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle, Long> {
    
    List<UserVehicle> findByUserId(Long userId);
    
    Optional<UserVehicle> findByIdAndUserId(Long id, Long userId);
    
    Optional<UserVehicle> findByVin(String vin);
    
    boolean existsByVin(String vin);
    
    boolean existsByVinAndUserId(String vin, Long userId);
    
    void deleteByIdAndUserId(Long id, Long userId);
}
