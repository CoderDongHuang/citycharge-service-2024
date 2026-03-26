package com.citycharge.repository;

import com.citycharge.entity.AlertLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertLogRepository extends JpaRepository<AlertLog, Long> {
    
    /**
     * 根据车辆编号查找报警记录
     */
    List<AlertLog> findByVidOrderByTimestampDesc(String vid);
    
    /**
     * 根据报警类型查找报警记录
     */
    List<AlertLog> findByTypeOrderByTimestampDesc(String type);
    
    /**
     * 根据报警级别查找报警记录
     */
    List<AlertLog> findByLevelOrderByTimestampDesc(String level);
    
    /**
     * 查找指定时间范围内的报警记录
     */
    List<AlertLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);
    
    /**
     * 检查指定时间内是否存在重复报警
     */
    @Query("SELECT COUNT(a) > 0 FROM AlertLog a WHERE a.vid = :vid AND a.type = :type AND a.timestamp >= :since")
    boolean existsRecentAlert(@Param("vid") String vid, @Param("type") String type, @Param("since") LocalDateTime since);
    
    /**
     * 查找未处理的报警记录
     */
    @Query("SELECT a FROM AlertLog a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<AlertLog> findRecentAlerts(@Param("since") LocalDateTime since);
    
    /**
     * 统计指定车辆的报警数量
     */
    @Query("SELECT COUNT(a) FROM AlertLog a WHERE a.vid = :vid")
    long countByVid(@Param("vid") String vid);
}