package com.citycharge.service;

import com.citycharge.dto.VehicleStatusMessage;
import com.citycharge.entity.Battery;
import com.citycharge.entity.Vehicle;
import com.citycharge.repository.BatteryRepository;
import com.citycharge.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttVehicleStatusService {
    
    private final VehicleRepository vehicleRepository;
    private final BatteryRepository batteryRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 处理车辆状态消息
     * @param topic MQTT主题
     * @param payload 消息内容
     */
    public void handleVehicleStatusMessage(String topic, String payload) {
        try {
            log.info("收到车辆状态消息，主题: {}, 内容: {}", topic, payload);
            
            // 解析JSON消息
            VehicleStatusMessage statusMessage = objectMapper.readValue(payload, VehicleStatusMessage.class);
            
            // 验证必填字段
            if (statusMessage.getVid() == null || statusMessage.getVid().isEmpty()) {
                log.error("车辆状态消息缺少vid字段");
                return;
            }
            
            // 处理车辆状态更新
            processVehicleStatus(statusMessage);
            
            log.info("车辆状态消息处理完成: {}", statusMessage.getVid());
            
        } catch (Exception e) {
            log.error("处理车辆状态消息失败，主题: {}, 错误: {}", topic, e.getMessage(), e);
        }
    }
    
    /**
     * 处理车辆状态更新
     */
    private void processVehicleStatus(VehicleStatusMessage statusMessage) {
        String vid = statusMessage.getVid();
        
        Vehicle vehicle = vehicleRepository.findByVid(vid)
                .orElseGet(() -> {
                    Vehicle newVehicle = new Vehicle();
                    newVehicle.setVid(vid);
                    newVehicle.setCreatedAt(LocalDateTime.now());
                    return newVehicle;
                });
        
        updateVehicleFields(vehicle, statusMessage);
        vehicleRepository.save(vehicle);
        
        if (statusMessage.getPid() != null && !statusMessage.getPid().isEmpty()) {
            updateBatteryStatus(statusMessage);
        }
        
        log.debug("车辆状态更新成功: {}", vid);
    }
    
    /**
     * 更新车辆字段
     */
    private void updateVehicleFields(Vehicle vehicle, VehicleStatusMessage statusMessage) {
        // 更新基础信息
        vehicle.setPid(statusMessage.getPid());
        vehicle.setVoltage(statusMessage.getVoltage());
        vehicle.setTemperature(statusMessage.getTemperature());
        vehicle.setBatteryLevel(statusMessage.getBatteryLevel());
        
        // 处理灯光状态（字符串转枚举）
        if (statusMessage.getLightStatus() != null) {
            try {
                Vehicle.LightStatus lightStatus = Vehicle.LightStatus.valueOf(statusMessage.getLightStatus());
                vehicle.setLightStatus(lightStatus);
            } catch (IllegalArgumentException e) {
                log.warn("无效的灯光状态: {}, 使用默认值off", statusMessage.getLightStatus());
                vehicle.setLightStatus(Vehicle.LightStatus.off);
            }
        }
        
        vehicle.setPositionX(statusMessage.getPositionX());
        vehicle.setPositionY(statusMessage.getPositionY());
        vehicle.setOnlineStatus(statusMessage.getOnlineStatus() != null && statusMessage.getOnlineStatus());
        
        // 更新时间戳
        if (statusMessage.getTimestamp() != null) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime timestamp = LocalDateTime.parse(statusMessage.getTimestamp(), formatter);
                vehicle.setLastUpdate(timestamp);
            } catch (Exception e) {
                log.warn("时间戳格式错误: {}, 使用当前时间", statusMessage.getTimestamp());
                vehicle.setLastUpdate(LocalDateTime.now());
            }
        } else {
            vehicle.setLastUpdate(LocalDateTime.now());
        }
        
        vehicle.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * 更新电池状态
     */
    private void updateBatteryStatus(VehicleStatusMessage statusMessage) {
        String pid = statusMessage.getPid();
        
        Battery battery = batteryRepository.findByPid(pid)
                .orElseGet(() -> {
                    Battery newBattery = new Battery();
                    newBattery.setPid(pid);
                    newBattery.setCreatedTime(LocalDateTime.now());
                    return newBattery;
                });
        
        battery.setVid(statusMessage.getVid());
        battery.setVoltage(statusMessage.getVoltage());
        battery.setTemperature(statusMessage.getTemperature());
        battery.setBatteryLevel(statusMessage.getBatteryLevel());
        battery.setStatus(calculateBatteryStatus(statusMessage));
        battery.setLastUpdate(LocalDateTime.now());
        
        batteryRepository.save(battery);
        log.debug("电池状态更新成功: {}", pid);
    }
    
    /**
     * 计算电池状态
     * 状态判断优先级: overheat > low_voltage > low > normal
     */
    private String calculateBatteryStatus(VehicleStatusMessage statusMessage) {
        Double temperature = statusMessage.getTemperature();
        Double voltage = statusMessage.getVoltage();
        Double batteryLevel = statusMessage.getBatteryLevel();
        
        if (temperature != null && temperature > 60.0) {
            return "overheat";
        }
        
        if (voltage != null && voltage < 3.0) {
            return "low_voltage";
        }
        
        if (batteryLevel != null && batteryLevel < 20.0) {
            return "low";
        }
        
        return "normal";
    }
}