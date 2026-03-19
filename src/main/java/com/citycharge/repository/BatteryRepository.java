package com.citycharge.repository;

import com.citycharge.entity.Battery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long> {
    
    Optional<Battery> findByPid(String pid);
    
    List<Battery> findByStatus(Battery.BatteryStatus status);
    
    @Query("SELECT b FROM Battery b WHERE b.currentVehicle = :vid")
    Optional<Battery> findByCurrentVehicle(@Param("vid") String vid);
    
    @Query("SELECT b FROM Battery b WHERE b.remainingCapacity < :threshold")
    List<Battery> findLowCapacityBatteries(@Param("threshold") Double threshold);
}