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
    
    /**
     * 根据电池编号查找历史记录，按时间倒序排列
     */
    List<BatteryHistory> findByPidOrderByTimestampDesc(String pid);
    
    /**
     * 查找指定时间范围内的历史记录
     */
    @Query("SELECT bh FROM BatteryHistory bh WHERE bh.pid = :pid AND bh.timestamp BETWEEN :startTime AND :endTime ORDER BY bh.timestamp DESC")
    List<BatteryHistory> findByPidAndTimestampBetween(@Param("pid") String pid, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最近的N条历史记录
     */
    @Query("SELECT bh FROM BatteryHistory bh WHERE bh.pid = :pid ORDER BY bh.timestamp DESC")
    List<BatteryHistory> findRecentByPid(@Param("pid") String pid, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 统计电池的历史记录数量
     */
    @Query("SELECT COUNT(bh) FROM BatteryHistory bh WHERE bh.pid = :pid")
    long countByPid(@Param("pid") String pid);
    
    /**
     * 查找指定状态的历史记录
     */
    List<BatteryHistory> findByPidAndStatusOrderByTimestampDesc(String pid, String status);
    
    /**
     * 查找电量变化趋势数据
     */
    @Query("SELECT bh FROM BatteryHistory bh WHERE bh.pid = :pid AND bh.timestamp >= :since ORDER BY bh.timestamp ASC")
    List<BatteryHistory> findTrendDataByPid(@Param("pid") String pid, @Param("since") LocalDateTime since);
    
    /**
     * 删除指定时间之前的历史记录（用于数据清理）
     */
    @Query("DELETE FROM BatteryHistory bh WHERE bh.timestamp < :threshold")
    void deleteByTimestampBefore(@Param("threshold") LocalDateTime threshold);
}