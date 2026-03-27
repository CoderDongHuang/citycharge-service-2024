package com.citycharge.repository;

import com.citycharge.entity.AlarmRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlarmRecordRepository extends JpaRepository<AlarmRecord, Long> {
    
    List<AlarmRecord> findByIsResolvedFalse();
    
    List<AlarmRecord> findByIsResolved(Boolean isResolved);
    
    List<AlarmRecord> findByAlarmType(String alarmType);
    
    @Query("SELECT a FROM AlarmRecord a WHERE a.vehicleVid = :vid ORDER BY a.alarmTime DESC")
    List<AlarmRecord> findRecentAlarmsByVehicle(@Param("vid") String vid);
    
    Long countByIsResolved(Boolean isResolved);
}