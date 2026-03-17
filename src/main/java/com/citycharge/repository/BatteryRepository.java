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
    
    List<Battery> findByIsInUseTrue();
    
    List<Battery> findByIsAvailableTrueAndIsInUseFalse();
    
    @Query("SELECT b FROM Battery b WHERE b.currentVid = :vid")
    Optional<Battery> findByCurrentVid(@Param("vid") String vid);
    
    @Query("SELECT b FROM Battery b WHERE b.capacityPercentage < :threshold")
    List<Battery> findLowCapacityBatteries(@Param("threshold") Double threshold);
}