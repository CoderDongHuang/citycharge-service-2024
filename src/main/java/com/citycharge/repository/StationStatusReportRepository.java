package com.citycharge.repository;

import com.citycharge.entity.StationStatusReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationStatusReportRepository extends JpaRepository<StationStatusReport, Long> {
    
    List<StationStatusReport> findByStationId(Long stationId);
    
    List<StationStatusReport> findByUserId(Long userId);
    
    List<StationStatusReport> findByStationIdAndStatus(Long stationId, StationStatusReport.ReportStatus status);
    
    @Query("SELECT r FROM StationStatusReport r WHERE r.stationId = :stationId ORDER BY r.createdAt DESC")
    List<StationStatusReport> findByStationIdOrderByCreatedAtDesc(@Param("stationId") Long stationId);
    
    @Query("SELECT COUNT(r) FROM StationStatusReport r WHERE r.status = :status")
    long countByStatus(@Param("status") StationStatusReport.ReportStatus status);
}
