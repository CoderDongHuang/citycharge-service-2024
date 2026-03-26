package com.citycharge.service;

import com.citycharge.dto.BatteryAlarmMessage;
import com.citycharge.entity.AlertLog;
import com.citycharge.entity.Vehicle;
import com.citycharge.repository.AlertLogRepository;
import com.citycharge.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BatteryAlarmService {
    
    private final VehicleRepository vehicleRepository;
    private final AlertLogRepository alertLogRepository;
    private final WebSocketService webSocketService;
    
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    /**
     * 处理电池报警消息
     * 
     * @param vid 车辆编号
     * @param alarmMsg 报警消息
     */
    @Transactional
    public void handleBatteryAlarm(String vid, BatteryAlarmMessage alarmMsg) {
        // 1. 验证设备存在性
        if (!vehicleRepository.existsByVid(vid)) {
            log.warn("收到未知设备的报警消息，设备编号: {}", vid);
            return;
        }
        
        // 2. 检查是否为重复报警（5分钟内相同类型的报警）
        if (isDuplicateAlert(vid, alarmMsg)) {
            log.info("忽略重复报警消息，设备: {}, 类型: {}", vid, alarmMsg.getType());
            return;
        }
        
        // 3. 记录到alert_log表
        AlertLog alertLog = createAlertLog(alarmMsg);
        alertLogRepository.save(alertLog);
        
        // 4. 推送到前端显示
        sendAlarmNotification(alarmMsg);
        
        log.info("处理电池报警成功 - 设备: {}, 类型: {}, 级别: {}", 
                vid, alarmMsg.getType(), alarmMsg.getLevel());
    }
    
    /**
     * 从MQTT主题中提取车辆编号
     */
    public String extractVidFromTopic(String topic) {
        try {
            // 主题格式: vehicle/{vid}/alarm
            String[] parts = topic.split("/");
            if (parts.length >= 2) {
                return parts[1];
            }
        } catch (Exception e) {
            log.error("从主题中提取车辆编号失败: {}", topic, e);
        }
        return null;
    }
    
    /**
     * 解析MQTT消息为BatteryAlarmMessage对象
     */
    public BatteryAlarmMessage parseMessage(String messagePayload) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(messagePayload, BatteryAlarmMessage.class);
        } catch (Exception e) {
            log.error("解析电池报警消息失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 检查是否为重复报警
     */
    private boolean isDuplicateAlert(String vid, BatteryAlarmMessage alarmMsg) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return alertLogRepository.existsRecentAlert(vid, alarmMsg.getType(), fiveMinutesAgo);
    }
    
    /**
     * 创建报警日志记录
     */
    private AlertLog createAlertLog(BatteryAlarmMessage alarmMsg) {
        AlertLog alertLog = new AlertLog();
        
        // 解析时间戳
        try {
            LocalDateTime timestamp = LocalDateTime.parse(alarmMsg.getTimestamp(), TIMESTAMP_FORMATTER);
            alertLog.setTimestamp(timestamp);
        } catch (Exception e) {
            log.warn("解析时间戳失败，使用当前时间: {}", alarmMsg.getTimestamp());
            alertLog.setTimestamp(LocalDateTime.now());
        }
        
        alertLog.setType(alarmMsg.getType());
        alertLog.setVid(alarmMsg.getVid());
        alertLog.setPid(alarmMsg.getPid());
        alertLog.setLevel(alarmMsg.getLevel());
        alertLog.setTriggerValue(alarmMsg.getTriggerValue());
        alertLog.setThresholdValue(alarmMsg.getThresholdValue());
        alertLog.setPositionX(alarmMsg.getPositionX());
        alertLog.setPositionY(alarmMsg.getPositionY());
        alertLog.setMessage(alarmMsg.getMessage());
        
        return alertLog;
    }
    
    /**
     * 发送报警通知到前端
     */
    private void sendAlarmNotification(BatteryAlarmMessage alarmMsg) {
        try {
            Map<String, Object> alarmNotification = new HashMap<>();
            alarmNotification.put("type", "battery_alarm");
            alarmNotification.put("vid", alarmMsg.getVid());
            alarmNotification.put("pid", alarmMsg.getPid());
            alarmNotification.put("alarmType", alarmMsg.getType());
            alarmNotification.put("level", alarmMsg.getLevel());
            alarmNotification.put("triggerValue", alarmMsg.getTriggerValue());
            alarmNotification.put("thresholdValue", alarmMsg.getThresholdValue());
            alarmNotification.put("message", alarmMsg.getMessage());
            alarmNotification.put("timestamp", alarmMsg.getTimestamp());
            
            // 推送到车辆特定的主题
            webSocketService.sendAlarmNotification(alarmNotification);
            
            // 同时广播到系统主题
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("type", "alarm_notification");
            systemMsg.put("vid", alarmMsg.getVid());
            systemMsg.put("alarmType", alarmMsg.getType());
            systemMsg.put("level", alarmMsg.getLevel());
            systemMsg.put("timestamp", LocalDateTime.now().toString());
            
            webSocketService.broadcastSystemMessage(systemMsg);
            
            log.debug("已推送报警通知到前端 - 车辆: {}, 类型: {}, 级别: {}", 
                    alarmMsg.getVid(), alarmMsg.getType(), alarmMsg.getLevel());
            
        } catch (Exception e) {
            log.error("推送报警通知到前端失败: {}", e.getMessage());
        }
    }
    
    /**
     * 根据触发值和阈值确定报警级别
     */
    public String determineAlertLevel(double triggerValue, double thresholdValue) {
        double difference = triggerValue - thresholdValue;
        double percentage = (difference / thresholdValue) * 100;
        
        if (percentage > 20) return "critical";
        else if (percentage > 10) return "high";
        else if (percentage > 5) return "medium";
        else return "low";
    }
    
    /**
     * 获取指定车辆的报警历史
     */
    public java.util.List<AlertLog> getAlarmHistory(String vid) {
        return alertLogRepository.findByVidOrderByTimestampDesc(vid);
    }
    
    /**
     * 获取最近未处理的报警记录
     */
    public java.util.List<AlertLog> getRecentAlerts(int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return alertLogRepository.findRecentAlerts(since);
    }
}