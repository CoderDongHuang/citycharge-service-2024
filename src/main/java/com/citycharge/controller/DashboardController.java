package com.citycharge.controller;

import com.citycharge.common.ApiResponse;
import com.citycharge.entity.AlarmRecord;
import com.citycharge.entity.Vehicle;
import com.citycharge.service.AlarmService;
import com.citycharge.service.VehicleService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final VehicleService vehicleService;
    private final AlarmService alarmService;
    
    @GetMapping("/overview")
    public ApiResponse<DashboardOverview> getOverview() {
        try {
            DashboardOverview overview = new DashboardOverview();
            
            // 获取在线车辆数量
            List<Vehicle> allVehicles = vehicleService.findAll();
            long onlineVehicles = allVehicles.stream()
                    .filter(vehicle -> Boolean.TRUE.equals(vehicle.getOnlineStatus()))
                    .count();
            overview.setOnlineVehicles((int) onlineVehicles);
            
            // 获取总电池数量（这里需要电池服务，暂时设为固定值）
            overview.setTotalBatteries(15); // 假设有15个电池
            
            // 获取活跃报警数量
            List<AlarmRecord> allAlerts = alarmService.findAll();
            long activeAlerts = allAlerts.stream()
                    .filter(alert -> !Boolean.TRUE.equals(alert.getIsResolved()))
                    .count();
            overview.setActiveAlerts((int) activeAlerts);
            
            // 获取换电站数量（暂时设为固定值）
            overview.setChargingStations(5); // 假设有5个换电站
            
            // 获取最近报警
            List<RecentAlert> recentAlerts = allAlerts.stream()
                    .filter(alert -> !Boolean.TRUE.equals(alert.getIsResolved()))
                    .sorted((a1, a2) -> a2.getAlarmTime().compareTo(a1.getAlarmTime()))
                    .limit(5)
                    .map(alert -> {
                        RecentAlert recentAlert = new RecentAlert();
                        recentAlert.setId(alert.getId());
                        recentAlert.setType(alert.getAlarmType());
                        recentAlert.setTimestamp(alert.getAlarmTime());
                        recentAlert.setVehicleId(alert.getVehicleVid());
                        return recentAlert;
                    })
                    .collect(Collectors.toList());
            overview.setRecentAlerts(recentAlerts);
            
            // 获取低电量车辆
            List<LowBatteryVehicle> lowBatteryVehicles = allVehicles.stream()
                    .filter(vehicle -> vehicle.getBatteryLevel() != null && vehicle.getBatteryLevel() < 20.0)
                    .map(vehicle -> {
                        LowBatteryVehicle lowBatteryVehicle = new LowBatteryVehicle();
                        lowBatteryVehicle.setVid(vehicle.getVid());
                        lowBatteryVehicle.setBatteryLevel(vehicle.getBatteryLevel());
                        return lowBatteryVehicle;
                    })
                    .collect(Collectors.toList());
            overview.setLowBatteryVehicles(lowBatteryVehicles);
            
            return ApiResponse.success(overview);
        } catch (Exception e) {
            return ApiResponse.error("获取系统概览失败: " + e.getMessage());
        }
    }
    
    @Data
    public static class DashboardOverview {
        private Integer onlineVehicles;
        private Integer totalBatteries;
        private Integer activeAlerts;
        private Integer chargingStations;
        private List<RecentAlert> recentAlerts;
        private List<LowBatteryVehicle> lowBatteryVehicles;
    }
    
    @Data
    public static class RecentAlert {
        private Long id;
        private String type;
        private LocalDateTime timestamp;
        private String vehicleId;
    }
    
    @Data
    public static class LowBatteryVehicle {
        private String vid;
        private Double batteryLevel;
    }
}