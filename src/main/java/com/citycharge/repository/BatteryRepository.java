package com.citycharge.repository;

import com.citycharge.entity.Battery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatteryRepository extends JpaRepository<Battery, Long> {
    
    /**
     * 根据电池编号查找电池
     */
    Optional<Battery> findByPid(String pid);
    
    /**
     * 根据车辆编号查找电池列表
     */
    List<Battery> findByVid(String vid);
    
    /**
     * 根据状态查找电池列表
     */
    List<Battery> findByStatus(String status);
    
    /**
     * 查找电量低于指定值的电池
     */
    @Query("SELECT b FROM Battery b WHERE b.batteryLevel < :level")
    List<Battery> findByBatteryLevelLessThan(@Param("level") Double level);
    
    /**
     * 查找温度高于指定值的电池
     */
    @Query("SELECT b FROM Battery b WHERE b.temperature > :temperature")
    List<Battery> findByTemperatureGreaterThan(@Param("temperature") Double temperature);
    
    /**
     * 查找最近更新的电池
     */
    @Query("SELECT b FROM Battery b ORDER BY b.lastUpdate DESC")
    Page<Battery> findRecentBatteries(Pageable pageable);
    
    /**
     * 统计电池数量
     */
    @Query("SELECT COUNT(b) FROM Battery b")
    long countBatteries();
    
    /**
     * 统计指定状态的电池数量
     */
    @Query("SELECT COUNT(b) FROM Battery b WHERE b.status = :status")
    long countByStatus(@Param("status") String status);
    
    /**
     * 检查电池是否存在
     */
    boolean existsByPid(String pid);
}