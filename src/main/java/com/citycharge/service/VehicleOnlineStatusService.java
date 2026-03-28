package com.citycharge.service;

import com.citycharge.dto.OnlineStatusMessage;
import com.citycharge.entity.Vehicle;
import com.citycharge.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 车辆在线状态服务
 * 处理车辆上线/下线状态更新
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleOnlineStatusService {
    
    private final VehicleRepository vehicleRepository;
    
    /**
     * 处理车辆在线状态消息
     * 只更新已存在设备的状态，不创建新设备
     * 
     * @param vid 车辆编号
     * @param statusMsg 在线状态消息
     */
    @Transactional
    public void handleOnlineStatus(String vid, OnlineStatusMessage statusMsg) {
        // 1. 检查设备是否存在
        Optional<Vehicle> vehicleOpt = vehicleRepository.findByVid(vid);
        
        if (!vehicleOpt.isPresent()) {
            // 设备不存在，记录警告日志但不创建新记录
            log.warn("收到未知设备的在线状态消息，设备编号: {}", vid);
            return;
        }
        
        // 2. 更新现有设备状态
        Vehicle vehicle = vehicleOpt.get();
        boolean oldStatus = vehicle.getOnlineStatus();
        updateVehicleStatus(vehicle, statusMsg);
        
        log.info("更新设备状态 - 设备: {}, 状态: {}", vid, statusMsg.getStatus());
        
        // 3. 通过WebSocket推送状态更新
        if (oldStatus != vehicle.getOnlineStatus()) {
            sendStatusUpdateToFrontend(vid, statusMsg);
        }
    }
    
    /**
     * 更新车辆状态
     */
    private void updateVehicleStatus(Vehicle vehicle, OnlineStatusMessage statusMsg) {
        boolean isOnline = statusMsg.isOnline();
        
        vehicle.setOnlineStatus(isOnline);
        vehicle.setLastOnlineTime(LocalDateTime.now());
        
        if (isOnline) {
            // 上线时更新IP地址
            vehicle.setIpAddress(statusMsg.getIp());
        } else {
            // 下线时记录原因（可选）
            if (statusMsg.getReason() != null) {
                log.info("设备下线原因 - 设备: {}, 原因: {}", vehicle.getVid(), statusMsg.getReason());
            }
        }
        
        vehicleRepository.save(vehicle);
    }
    
    /**
     * 从MQTT主题中提取车辆编号
     * 主题格式: vehicle/{vid}/online
     * 
     * @param topic MQTT主题
     * @return 车辆编号
     */
    public String extractVidFromTopic(String topic) {
        if (topic == null || !topic.startsWith("vehicle/")) {
            return null;
        }
        
        String[] parts = topic.split("/");
        if (parts.length >= 2) {
            return parts[1]; // 返回vehicle/后面的部分
        }
        
        return null;
    }
    
    /**
     * 解析MQTT消息为OnlineStatusMessage对象
     * 
     * @param messagePayload MQTT消息内容
     * @return 解析后的消息对象
     */
    public OnlineStatusMessage parseMessage(String messagePayload) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(messagePayload, OnlineStatusMessage.class);
        } catch (Exception e) {
            log.error("解析在线状态消息失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 发送状态更新到前端
     * 
     * @param vid 车辆编号
     * @param statusMsg 在线状态消息
     */
    private void sendStatusUpdateToFrontend(String vid, OnlineStatusMessage statusMsg) {
        try {
            Map<String, Object> statusUpdate = new HashMap<>();
            statusUpdate.put("vid", vid);
            statusUpdate.put("status", statusMsg.getStatus());
            statusUpdate.put("timestamp", statusMsg.getTimestamp());
            statusUpdate.put("type", "online_status");
            
            if (statusMsg.isOnline()) {
                statusUpdate.put("ip", statusMsg.getIp());
            } else {
                statusUpdate.put("reason", statusMsg.getReason());
            }
            
            // 暂时不推送 WebSocket，等待后续实现
            log.debug("车辆状态更新 - 车辆：{}, 状态：{}", vid, statusMsg.getStatus());
            
        } catch (Exception e) {
            log.error("推送状态更新到前端失败: {}", e.getMessage());
        }
    }
}