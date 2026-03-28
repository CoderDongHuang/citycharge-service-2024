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
        
        // 4. 特殊处理：电量过低报警需要额外操作
        if (alarmMsg.isLowBatteryAlarm()) {
            handleLowBatterySpecialAction(vid, alarmMsg);
        }
        
        // 5. 推送到前端显示
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
            BatteryAlarmMessage alarmMessage = mapper.readValue(messagePayload, BatteryAlarmMessage.class);
            
            // 修复消息字段的中文编码
            if (alarmMessage.getMessage() != null) {
                alarmMessage.setMessage(fixChineseText(alarmMessage.getMessage()));
            }
            
            return alarmMessage;
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
            
            // 暂时不推送 WebSocket，等待后续实现
            log.debug("收到报警通知 - 车辆：{}, 类型：{}, 级别：{}", 
                    alarmMsg.getVid(), alarmMsg.getType(), alarmMsg.getLevel());
            
        } catch (Exception e) {
            log.error("推送报警通知到前端失败: {}", e.getMessage());
        }
    }
    
    /**
     * 根据触发值和阈值确定报警级别
     * 对于电量过低报警，触发值低于阈值时计算差值
     */
    public String determineAlertLevel(double triggerValue, double thresholdValue, String alarmType) {
        double difference;
        
        if ("lowBattery".equals(alarmType)) {
            // 电量过低：触发值 < 阈值，计算负差值
            difference = thresholdValue - triggerValue;
        } else {
            // 其他报警：触发值 > 阈值，计算正差值
            difference = triggerValue - thresholdValue;
        }
        
        double percentage = (difference / thresholdValue) * 100;
        
        if (percentage > 20) return "critical";
        else if (percentage > 10) return "high";
        else if (percentage > 5) return "medium";
        else return "low";
    }
    
    /**
     * 重载方法，兼容原有调用
     */
    public String determineAlertLevel(double triggerValue, double thresholdValue) {
        return determineAlertLevel(triggerValue, thresholdValue, "temperature");
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
    
    /**
     * 处理电量过低报警的特殊操作
     * - 更新车辆电量状态
     * - 发送紧急通知
     * - 记录特殊处理日志
     */
    private void handleLowBatterySpecialAction(String vid, BatteryAlarmMessage alarmMsg) {
        try {
            // 1. 更新车辆电量状态
            Optional<Vehicle> vehicleOpt = vehicleRepository.findByVid(vid);
            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                vehicle.setBatteryLevel(alarmMsg.getTriggerValue());
                vehicleRepository.save(vehicle);
                
                log.info("更新车辆电量状态 - 车辆: {}, 电量: {}%", vid, alarmMsg.getTriggerValue());
            }
            
            // 2. 发送紧急通知（如果是严重级别）
            if (alarmMsg.isCriticalLevel()) {
                sendEmergencyNotification(vid, alarmMsg);
            }
            
            // 3. 记录特殊处理日志
            log.warn("电量过低报警特殊处理 - 车辆: {}, 触发值: {}, 阈值: {}", 
                    vid, alarmMsg.getTriggerValue(), alarmMsg.getThresholdValue());
                    
        } catch (Exception e) {
            log.error("处理电量过低报警特殊操作失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 发送紧急通知
     */
    private void sendEmergencyNotification(String vid, BatteryAlarmMessage alarmMsg) {
        try {
            Map<String, Object> emergencyMsg = new HashMap<>();
            emergencyMsg.put("type", "emergency_notification");
            emergencyMsg.put("vid", vid);
            emergencyMsg.put("alarmType", "lowBattery");
            emergencyMsg.put("level", "critical");
            emergencyMsg.put("triggerValue", alarmMsg.getTriggerValue());
            emergencyMsg.put("thresholdValue", alarmMsg.getThresholdValue());
            emergencyMsg.put("message", "电量严重不足，请立即处理！");
            emergencyMsg.put("timestamp", LocalDateTime.now().toString());
            
            // 暂时不推送 WebSocket，等待后续实现
            
            log.info("检测到紧急低电量 - 车辆：{}, 电量：{}%", vid, alarmMsg.getTriggerValue());
            
        } catch (Exception e) {
            log.error("发送紧急通知失败: {}", e.getMessage());
        }
    }
    
    /**
     * 智能修复中文文本
     * 使用编码检测和智能替换，避免硬编码
     */
    private String fixChineseText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "设备报警";
        }
        
        // 1. 检查是否是正常中文
        if (isValidChinese(text)) {
            return text;
        }
        
        // 2. 检查是否是乱码中文（UTF-8被误读为ISO-8859-1）
        String fixedText = tryFixUTF8ToISO88591(text);
        if (isValidChinese(fixedText)) {
            log.info("成功修复中文乱码: {} -> {}", text, fixedText);
            return fixedText;
        }
        
        // 3. 检查是否是问号乱码
        if (text.contains("?")) {
            log.warn("检测到问号乱码，返回默认消息: {}", text);
            return "设备报警";
        }
        
        // 4. 尝试根据报警类型推断消息内容
        return inferAlarmMessage(text);
    }
    
    /**
     * 检查文本是否是有效的中文
     */
    private boolean isValidChinese(String text) {
        // 检查是否包含中文字符（Unicode范围：4E00-9FFF）
        return text.matches(".*[\\u4e00-\\u9fff].*");
    }
    
    /**
     * 尝试修复UTF-8被误读为ISO-8859-1的乱码
     */
    private String tryFixUTF8ToISO88591(String garbledText) {
        try {
            // 将乱码文本按ISO-8859-1编码获取字节，然后按UTF-8解码
            byte[] bytes = garbledText.getBytes("ISO-8859-1");
            return new String(bytes, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            return garbledText;
        }
    }
    
    /**
     * 根据报警类型推断消息内容
     */
    private String inferAlarmMessage(String garbledText) {
        // 如果是已知的乱码模式，进行智能替换
        if (garbledText.contains("鐢垫睜")) {
            if (garbledText.contains("鐢甸噺")) return "电池电量过低";
            if (garbledText.contains("娓╁害")) return "电池温度异常";
            if (garbledText.contains("鐢靛帇")) return "电池电压异常";
            return "电池异常";
        }
        
        if (garbledText.contains("鍏呯數")) {
            return "充电异常";
        }
        
        // 无法识别的乱码，返回通用消息
        log.warn("无法识别的乱码模式: {}", garbledText);
        return "设备报警";
    }
}