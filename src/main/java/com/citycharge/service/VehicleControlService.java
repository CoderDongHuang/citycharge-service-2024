package com.citycharge.service;

import com.citycharge.config.MqttMessageListenerConfig;
import com.citycharge.dto.ControlCommand;
import com.citycharge.entity.Vehicle;
import com.citycharge.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleControlService {
    
    private final VehicleRepository vehicleRepository;
    private final MqttMessageListenerConfig mqttConfig;
    
    /**
     * 发送车辆控制指令
     * @param vid 车辆编号
     * @param command 控制指令
     * @return 发送结果
     */
    public boolean sendControlCommand(String vid, ControlCommand command) {
        try {
            // 验证车辆是否存在
            Optional<Vehicle> vehicleOpt = vehicleRepository.findByVid(vid);
            if (!vehicleOpt.isPresent()) {
                log.error("车辆不存在: {}", vid);
                return false;
            }
            
            Vehicle vehicle = vehicleOpt.get();
            
            // 验证车辆是否在线
            if (!Boolean.TRUE.equals(vehicle.getOnlineStatus())) {
                log.warn("车辆不在线，无法发送控制指令: {}", vid);
                return false;
            }
            
            // 获取MQTT客户端
            MqttClient mqttClient = mqttConfig.getMqttClient();
            if (mqttClient == null || !mqttClient.isConnected()) {
                log.error("MQTT客户端未连接");
                return false;
            }
            
            // 构建MQTT主题
            String topic = "vehicle/" + vid + "/control";
            
            // 转换指令为JSON
            String payload = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(command);
            
            // 发送MQTT消息
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1); // 确保消息送达
            
            mqttClient.publish(topic, message);
            
            log.info("控制指令发送成功 - 车辆: {}, 指令: {}, 主题: {}", vid, command.getCommand(), topic);
            
            // 记录操作日志
            logOperation(vid, command);
            
            return true;
            
        } catch (Exception e) {
            log.error("发送控制指令失败 - 车辆: {}, 错误: {}", vid, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 灯光控制指令
     */
    public boolean controlLights(String vid, String lightStatus) {
        ControlCommand command = new ControlCommand();
        command.setCommand("setLightStatus");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("status", lightStatus);
        command.setParams(params);
        
        return sendControlCommand(vid, command);
    }
    
    /**
     * 闪烁灯光指令
     */
    public boolean flashLights(String vid, String pattern, int duration) {
        ControlCommand command = new ControlCommand();
        command.setCommand("flashLights");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("pattern", pattern);
        params.put("duration", duration);
        command.setParams(params);
        
        return sendControlCommand(vid, command);
    }
    
    /**
     * 设置在线状态
     */
    public boolean setOnlineStatus(String vid, boolean online) {
        ControlCommand command = new ControlCommand();
        command.setCommand("setOnlineStatus");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("online", online);
        command.setParams(params);
        
        return sendControlCommand(vid, command);
    }
    
    /**
     * 喇叭控制指令
     * @param vid 车辆编号
     * @param pattern 鸣笛模式 (single, double, triple, continuous)
     * @param interval 间隔时间(毫秒)
     * @return 发送结果
     */
    public boolean beepHorn(String vid, String pattern, int interval) {
        ControlCommand command = new ControlCommand();
        command.setCommand("beepHorn");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("pattern", pattern);
        params.put("interval", interval);
        command.setParams(params);
        
        return sendControlCommand(vid, command);
    }
    
    /**
     * 位置设置指令
     * @param vid 车辆编号
     * @param x X坐标
     * @param y Y坐标
     * @return 发送结果
     */
    public boolean setPosition(String vid, double x, double y) {
        ControlCommand command = new ControlCommand();
        command.setCommand("setPosition");
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("x", x);
        params.put("y", y);
        command.setParams(params);
        
        return sendControlCommand(vid, command);
    }
    
    /**
     * 记录操作日志
     */
    private void logOperation(String vid, ControlCommand command) {
        log.info("控制操作记录 - 车辆: {}, 指令: {}, 时间: {}", 
                vid, command.getCommand(), LocalDateTime.now());
    }
    
    /**
     * 验证控制指令参数
     */
    public boolean validateCommand(ControlCommand command) {
        if (command.getCommand() == null || command.getCommand().isEmpty()) {
            return false;
        }
        
        // 根据指令类型验证参数
        switch (command.getCommand()) {
            case "flashLights":
                return validateFlashLightsParams(command.getParams());
            case "setLightStatus":
                return validateLightStatusParams(command.getParams());
            case "setOnlineStatus":
                return validateOnlineStatusParams(command.getParams());
            case "beepHorn":
                return validateBeepHornParams(command.getParams());
            case "setPosition":
                return validateSetPositionParams(command.getParams());
            default:
                log.warn("未知的控制指令: {}", command.getCommand());
                return false;
        }
    }
    
    private boolean validateFlashLightsParams(java.util.Map<String, Object> params) {
        if (params == null) return false;
        
        String pattern = (String) params.get("pattern");
        Integer duration = (Integer) params.get("duration");
        
        return pattern != null && !pattern.isEmpty() && 
               duration != null && duration > 0 && duration <= 10000;
    }
    
    private boolean validateLightStatusParams(java.util.Map<String, Object> params) {
        if (params == null) return false;
        
        String status = (String) params.get("status");
        return status != null && (status.equals("off") || 
                                 status.equals("lowBeam") || 
                                 status.equals("highBeam"));
    }
    
    private boolean validateOnlineStatusParams(java.util.Map<String, Object> params) {
        if (params == null) return false;
        
        Boolean online = (Boolean) params.get("online");
        return online != null;
    }
    
    private boolean validateBeepHornParams(java.util.Map<String, Object> params) {
        if (params == null) return false;
        
        String pattern = (String) params.get("pattern");
        Integer interval = (Integer) params.get("interval");
        
        // 验证模式参数
        if (pattern == null || pattern.isEmpty()) {
            return false;
        }
        
        // 支持的模式类型 (使用Java 8兼容的方式)
        java.util.Set<String> validPatterns = new java.util.HashSet<>();
        validPatterns.add("single");
        validPatterns.add("double");
        validPatterns.add("triple");
        validPatterns.add("continuous");
        
        if (!validPatterns.contains(pattern)) {
            log.warn("无效的喇叭模式: {}", pattern);
            return false;
        }
        
        // 验证间隔参数
        if (interval == null || interval < 100 || interval > 5000) {
            log.warn("无效的间隔时间: {}ms (范围: 100-5000ms)", interval);
            return false;
        }
        
        return true;
    }
    
    private boolean validateSetPositionParams(java.util.Map<String, Object> params) {
        if (params == null) return false;
        
        Double x = null;
        Double y = null;
        
        // 处理不同类型的数值参数
        Object xObj = params.get("x");
        Object yObj = params.get("y");
        
        if (xObj instanceof Integer) {
            x = ((Integer) xObj).doubleValue();
        } else if (xObj instanceof Double) {
            x = (Double) xObj;
        }
        
        if (yObj instanceof Integer) {
            y = ((Integer) yObj).doubleValue();
        } else if (yObj instanceof Double) {
            y = (Double) yObj;
        }
        
        // 验证坐标参数
        if (x == null || y == null) {
            log.warn("位置坐标参数缺失或格式错误");
            return false;
        }
        
        // 验证坐标范围 (可根据实际地图范围调整)
        if (x < -180 || x > 180 || y < -90 || y > 90) {
            log.warn("无效的坐标范围: x={}, y={} (有效范围: x[-180,180], y[-90,90])", x, y);
            return false;
        }
        
        return true;
    }
}