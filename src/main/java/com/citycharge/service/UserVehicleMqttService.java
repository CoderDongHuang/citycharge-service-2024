package com.citycharge.service;

import com.citycharge.dto.UserVehicleStatusMessage;
import com.citycharge.entity.UserBattery;
import com.citycharge.entity.UserVehicle;
import com.citycharge.repository.UserBatteryRepository;
import com.citycharge.repository.UserVehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserVehicleMqttService {
    
    private final UserVehicleRepository userVehicleRepository;
    private final UserBatteryRepository userBatteryRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    
    public Long extractVehicleIdFromTopic(String topic) {
        try {
            String[] parts = topic.split("/");
            if (parts.length >= 3 && "user".equals(parts[0]) && "vehicle".equals(parts[1])) {
                return Long.parseLong(parts[2]);
            }
        } catch (Exception e) {
            log.error("解析车辆ID失败，主题: {}", topic, e);
        }
        return null;
    }
    
    public UserVehicleStatusMessage parseMessage(String payload) {
        try {
            return objectMapper.readValue(payload, UserVehicleStatusMessage.class);
        } catch (Exception e) {
            log.error("解析用户车辆状态消息失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Transactional
    public void handleUserVehicleStatus(String topic, String payload) {
        try {
            Long vehicleId = extractVehicleIdFromTopic(topic);
            if (vehicleId == null) {
                log.warn("无法从主题中提取车辆ID: {}", topic);
                return;
            }
            
            UserVehicleStatusMessage message = parseMessage(payload);
            if (message == null) {
                log.error("解析用户车辆状态消息失败，主题: {}", topic);
                return;
            }
            
            log.info("收到用户车辆状态消息，车辆ID: {}, 用户ID: {}", vehicleId, message.getUserId());
            
            Optional<UserVehicle> vehicleOpt = userVehicleRepository.findById(vehicleId);
            if (!vehicleOpt.isPresent()) {
                log.warn("车辆不存在，车辆ID: {}", vehicleId);
                return;
            }
            
            UserVehicle vehicle = vehicleOpt.get();
            
            if (message.getVehicle() != null) {
                UserVehicleStatusMessage.VehicleInfo vehicleInfo = message.getVehicle();
                
                if (vehicleInfo.getStatus() != null) {
                    vehicle.setStatus(UserVehicle.VehicleStatus.valueOf(vehicleInfo.getStatus()));
                }
                if (vehicleInfo.getBatteryLevel() != null) {
                    vehicle.setBatteryLevel(vehicleInfo.getBatteryLevel());
                }
                if (vehicleInfo.getVoltage() != null) {
                    vehicle.setVoltage(vehicleInfo.getVoltage());
                }
                if (vehicleInfo.getTemperature() != null) {
                    vehicle.setTemperature(vehicleInfo.getTemperature());
                }
                if (vehicleInfo.getLatitude() != null) {
                    vehicle.setLatitude(vehicleInfo.getLatitude());
                }
                if (vehicleInfo.getLongitude() != null) {
                    vehicle.setLongitude(vehicleInfo.getLongitude());
                }
                
                if ("online".equals(vehicleInfo.getStatus())) {
                    vehicle.setLastOnlineTime(LocalDateTime.now());
                }
                
                userVehicleRepository.save(vehicle);
                log.info("更新车辆状态成功，车辆ID: {}", vehicleId);
            }
            
            if (message.getBattery() != null) {
                UserVehicleStatusMessage.BatteryInfo batteryInfo = message.getBattery();
                
                if (batteryInfo.getId() != null) {
                    Optional<UserBattery> batteryOpt = userBatteryRepository.findById(batteryInfo.getId());
                    if (batteryOpt.isPresent()) {
                        UserBattery battery = batteryOpt.get();
                        
                        if (batteryInfo.getVoltage() != null) {
                            battery.setVoltage(batteryInfo.getVoltage());
                        }
                        if (batteryInfo.getTemperature() != null) {
                            battery.setTemperature(batteryInfo.getTemperature());
                        }
                        if (batteryInfo.getCurrentLevel() != null) {
                            battery.setCurrentLevel(batteryInfo.getCurrentLevel());
                        }
                        if (batteryInfo.getStatus() != null) {
                            battery.setStatus(UserBattery.BatteryStatus.valueOf(batteryInfo.getStatus()));
                        }
                        
                        battery.setCurrentVehicleId(vehicleId);
                        
                        userBatteryRepository.save(battery);
                        log.info("更新电池状态成功，电池ID: {}", batteryInfo.getId());
                    } else {
                        log.warn("电池不存在，电池ID: {}", batteryInfo.getId());
                    }
                } else if (batteryInfo.getCode() != null) {
                    Optional<UserBattery> batteryOpt = userBatteryRepository.findByCode(batteryInfo.getCode());
                    if (batteryOpt.isPresent()) {
                        UserBattery battery = batteryOpt.get();
                        
                        if (batteryInfo.getVoltage() != null) {
                            battery.setVoltage(batteryInfo.getVoltage());
                        }
                        if (batteryInfo.getTemperature() != null) {
                            battery.setTemperature(batteryInfo.getTemperature());
                        }
                        if (batteryInfo.getCurrentLevel() != null) {
                            battery.setCurrentLevel(batteryInfo.getCurrentLevel());
                        }
                        if (batteryInfo.getStatus() != null) {
                            battery.setStatus(UserBattery.BatteryStatus.valueOf(batteryInfo.getStatus()));
                        }
                        
                        battery.setCurrentVehicleId(vehicleId);
                        
                        userBatteryRepository.save(battery);
                        log.info("更新电池状态成功，电池编码: {}", batteryInfo.getCode());
                    } else {
                        log.warn("电池不存在，电池编码: {}", batteryInfo.getCode());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("处理用户车辆状态消息失败，主题: {}, 错误: {}", topic, e.getMessage(), e);
        }
    }
}
