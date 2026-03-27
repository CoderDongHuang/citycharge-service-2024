package com.citycharge.service;

import com.citycharge.entity.Battery;
import com.citycharge.entity.BatteryHistory;
import com.citycharge.repository.BatteryHistoryRepository;
import com.citycharge.repository.BatteryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatteryService {
    
    private final BatteryRepository batteryRepository;
    private final BatteryHistoryRepository batteryHistoryRepository;
    
    /**
     * 获取电池列表（分页）
     */
    public Page<Battery> getBatteries(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return batteryRepository.findAll(pageable);
    }
    
    /**
     * 根据电池编号获取电池信息
     */
    public Optional<Battery> getBatteryByPid(String pid) {
        return batteryRepository.findByPid(pid);
    }
    
    /**
     * 根据车辆编号获取电池列表
     */
    public List<Battery> getBatteriesByVid(String vid) {
        return batteryRepository.findByVid(vid);
    }
    
    /**
     * 获取电池历史记录
     */
    public List<BatteryHistory> getBatteryHistory(String pid, LocalDateTime startTime, LocalDateTime endTime, Integer limit) {
        if (startTime != null && endTime != null) {
            // 指定时间范围
            return batteryHistoryRepository.findByPidAndTimestampBetween(pid, startTime, endTime);
        } else if (limit != null) {
            // 指定记录数量
            Pageable pageable = PageRequest.of(0, limit);
            return batteryHistoryRepository.findRecentByPid(pid, pageable);
        } else {
            // 默认返回最近100条记录
            Pageable pageable = PageRequest.of(0, 100);
            return batteryHistoryRepository.findRecentByPid(pid, pageable);
        }
    }
    
    /**
     * 更新电池状态（从车辆状态消息中同步）
     */
    @Transactional
    public void updateBatteryStatus(String pid, String vid, Double voltage, Double temperature, Double batteryLevel) {
        try {
            Optional<Battery> batteryOpt = batteryRepository.findByPid(pid);
            Battery battery;
            
            if (batteryOpt.isPresent()) {
                // 更新现有电池
                battery = batteryOpt.get();
                battery.setVid(vid);
                battery.setVoltage(voltage);
                battery.setTemperature(temperature);
                battery.setBatteryLevel(batteryLevel);
                battery.setLastUpdate(LocalDateTime.now());
                
                // 根据状态值更新状态字段
                updateBatteryStatusField(battery);
                
            } else {
                // 创建新电池记录
                battery = new Battery();
                battery.setPid(pid);
                battery.setVid(vid);
                battery.setVoltage(voltage);
                battery.setTemperature(temperature);
                battery.setBatteryLevel(batteryLevel);
                battery.setLastUpdate(LocalDateTime.now());
                
                // 根据状态值更新状态字段
                updateBatteryStatusField(battery);
            }
            
            batteryRepository.save(battery);
            
            // 记录历史数据
            recordBatteryHistory(battery);
            
            log.debug("更新电池状态成功 - 电池: {}, 电量: {}%", pid, batteryLevel);
            
        } catch (Exception e) {
            log.error("更新电池状态失败 - 电池: {}, 错误: {}", pid, e.getMessage(), e);
        }
    }
    
    /**
     * 根据电池参数更新状态字段
     */
    private void updateBatteryStatusField(Battery battery) {
        if (battery.getBatteryLevel() != null && battery.getBatteryLevel() < 20) {
            battery.setStatus("low");
        } else if (battery.getTemperature() != null && battery.getTemperature() > 60) {
            battery.setStatus("overheat");
        } else if (battery.getVoltage() != null && battery.getVoltage() < 3.0) {
            battery.setStatus("low_voltage");
        } else {
            battery.setStatus("normal");
        }
    }
    
    /**
     * 记录电池历史数据
     */
    private void recordBatteryHistory(Battery battery) {
        try {
            BatteryHistory history = new BatteryHistory();
            history.setPid(battery.getPid());
            history.setVoltage(battery.getVoltage());
            history.setTemperature(battery.getTemperature());
            history.setBatteryLevel(battery.getBatteryLevel());
            history.setStatus(battery.getStatus());
            history.setTimestamp(LocalDateTime.now());
            
            batteryHistoryRepository.save(history);
            
        } catch (Exception e) {
            log.error("记录电池历史数据失败 - 电池: {}, 错误: {}", battery.getPid(), e.getMessage());
        }
    }
    
    /**
     * 获取电池统计信息
     */
    public BatteryStatistics getBatteryStatistics() {
        BatteryStatistics statistics = new BatteryStatistics();
        
        statistics.setTotalBatteries(batteryRepository.countBatteries());
        statistics.setNormalBatteries(batteryRepository.countByStatus("normal"));
        statistics.setLowBatteries(batteryRepository.countByStatus("low"));
        statistics.setOverheatBatteries(batteryRepository.countByStatus("overheat"));
        statistics.setLowVoltageBatteries(batteryRepository.countByStatus("low_voltage"));
        
        // 获取低电量电池列表
        statistics.setLowBatteryList(batteryRepository.findByBatteryLevelLessThan(20.0));
        
        // 获取高温电池列表
        statistics.setOverheatBatteryList(batteryRepository.findByTemperatureGreaterThan(60.0));
        
        return statistics;
    }
    
    /**
     * 电池统计信息类
     */
    public static class BatteryStatistics {
        private long totalBatteries;
        private long normalBatteries;
        private long lowBatteries;
        private long overheatBatteries;
        private long lowVoltageBatteries;
        private List<Battery> lowBatteryList;
        private List<Battery> overheatBatteryList;
        
        // Getter and Setter methods
        public long getTotalBatteries() { return totalBatteries; }
        public void setTotalBatteries(long totalBatteries) { this.totalBatteries = totalBatteries; }
        
        public long getNormalBatteries() { return normalBatteries; }
        public void setNormalBatteries(long normalBatteries) { this.normalBatteries = normalBatteries; }
        
        public long getLowBatteries() { return lowBatteries; }
        public void setLowBatteries(long lowBatteries) { this.lowBatteries = lowBatteries; }
        
        public long getOverheatBatteries() { return overheatBatteries; }
        public void setOverheatBatteries(long overheatBatteries) { this.overheatBatteries = overheatBatteries; }
        
        public long getLowVoltageBatteries() { return lowVoltageBatteries; }
        public void setLowVoltageBatteries(long lowVoltageBatteries) { this.lowVoltageBatteries = lowVoltageBatteries; }
        
        public List<Battery> getLowBatteryList() { return lowBatteryList; }
        public void setLowBatteryList(List<Battery> lowBatteryList) { this.lowBatteryList = lowBatteryList; }
        
        public List<Battery> getOverheatBatteryList() { return overheatBatteryList; }
        public void setOverheatBatteryList(List<Battery> overheatBatteryList) { this.overheatBatteryList = overheatBatteryList; }
    }
}