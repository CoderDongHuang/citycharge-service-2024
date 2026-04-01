package com.citycharge.repository;

import com.citycharge.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    
    Optional<Station> findByStationId(String stationId);
    
    List<Station> findByStatus(Station.Status status);
    
    List<Station> findByNameContaining(String name);
    
    @Query("SELECT s FROM Station s WHERE s.status = :status AND (s.name LIKE %:keyword% OR s.address LIKE %:keyword%)")
    List<Station> findByStatusAndKeyword(@Param("status") Station.Status status, @Param("keyword") String keyword);
    
    @Query("SELECT s FROM Station s WHERE s.name LIKE %:keyword% OR s.address LIKE %:keyword%")
    List<Station> findByKeyword(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(s) FROM Station s WHERE s.status = :status")
    long countByStatus(@Param("status") Station.Status status);
    
    @Query("SELECT SUM(s.batteryCapacity) FROM Station s")
    Long sumTotalCapacity();
    
    @Query("SELECT SUM(s.availableBatteries) FROM Station s")
    Long sumAvailableBatteries();
    
    boolean existsByStationId(String stationId);
}
