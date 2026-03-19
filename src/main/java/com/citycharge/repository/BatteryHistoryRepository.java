package com.citycharge.repository;

import com.citycharge.entity.BatteryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BatteryHistoryRepository extends JpaRepository<BatteryHistory, Long> {
    
    List<BatteryHistory> findByPidOrderByTimestampDesc(String pid);
    
    @Query("SELECT bh FROM BatteryHistory bh WHERE bh.pid = :pid AND bh.timestamp BETWEEN :start AND :end ORDER BY bh.timestamp ASC")
    List<BatteryHistory> findByPidAndTimeRange(@Param("pid") String pid, 
                                              @Param("start") LocalDateTime start, 
                                              @Param("end") LocalDateTime end);
    
    @Query("SELECT bh FROM BatteryHistory bh WHERE bh.vid = :vid ORDER BY bh.timestamp DESC")
    List<BatteryHistory> findByVid(@Param("vid") String vid);
}