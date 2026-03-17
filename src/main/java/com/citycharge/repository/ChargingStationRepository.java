package com.citycharge.repository;

import com.citycharge.entity.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
    
    Optional<ChargingStation> findByStationId(String stationId);
    
    List<ChargingStation> findByIsActiveTrue();
    
    @Query("SELECT cs FROM ChargingStation cs WHERE cs.availableBatteries > 0")
    List<ChargingStation> findStationsWithAvailableBatteries();
    
    @Query("SELECT cs FROM ChargingStation cs WHERE cs.positionX = :x AND cs.positionY = :y")
    Optional<ChargingStation> findByPosition(@Param("x") Integer x, @Param("y") Integer y);
}