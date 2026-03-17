package com.citycharge.repository;

import com.citycharge.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    Optional<Vehicle> findByVid(String vid);
    
    List<Vehicle> findByIsOnlineTrue();
    
    @Query("SELECT v FROM Vehicle v WHERE v.lastHeartbeat < :threshold")
    List<Vehicle> findOfflineVehicles(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT v FROM Vehicle v WHERE v.isAlarming = true")
    List<Vehicle> findAlarmingVehicles();
    
    @Query("SELECT v FROM Vehicle v WHERE v.positionX = :x AND v.positionY = :y")
    Optional<Vehicle> findByPosition(@Param("x") Integer x, @Param("y") Integer y);
}