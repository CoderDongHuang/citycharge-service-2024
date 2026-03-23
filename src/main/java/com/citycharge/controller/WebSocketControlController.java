package com.citycharge.controller;

import com.citycharge.dto.ControlCommand;
import com.citycharge.dto.WebSocketControlMessage;
import com.citycharge.service.VehicleControlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketControlController {
    
    private final VehicleControlService vehicleControlService;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 处理WebSocket控制消息
     */
    @MessageMapping("/vehicle.control")
    @SendToUser("/queue/control.result")
    public Map<String, Object> handleControlMessage(@Payload WebSocketControlMessage message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("收到WebSocket控制消息: {}", message);
            
            // 验证消息类型
            if (!"vehicle_control".equals(message.getType())) {
                response.put("success", false);
                response.put("error", "无效的消息类型");
                return response;
            }
            
            // 验证车辆编号
            if (message.getVid() == null || message.getVid().isEmpty()) {
                response.put("success", false);
                response.put("error", "车辆编号不能为空");
                return response;
            }
            
            // 验证控制指令
            if (message.getCommand() == null) {
                response.put("success", false);
                response.put("error", "控制指令不能为空");
                return response;
            }
            
            ControlCommand command = message.getCommand();
            
            // 验证指令参数
            if (!vehicleControlService.validateCommand(command)) {
                response.put("success", false);
                response.put("error", "控制指令参数无效");
                return response;
            }
            
            // 发送控制指令
            boolean success = vehicleControlService.sendControlCommand(message.getVid(), command);
            
            if (success) {
                response.put("success", true);
                response.put("message", "控制指令发送成功");
                response.put("messageId", message.getMessageId());
                response.put("vid", message.getVid());
                response.put("command", command.getCommand());
                
                // 广播控制操作（可选）
                broadcastControlOperation(message.getVid(), command);
                
            } else {
                response.put("success", false);
                response.put("error", "控制指令发送失败，车辆可能不在线");
            }
            
        } catch (Exception e) {
            log.error("处理WebSocket控制消息失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "处理控制消息失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 处理灯光控制消息
     */
    @MessageMapping("/vehicle.control.lights")
    @SendToUser("/queue/control.result")
    public Map<String, Object> handleLightControl(@Payload Map<String, Object> message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String vid = (String) message.get("vid");
            String status = (String) message.get("status");
            
            if (vid == null || status == null) {
                response.put("success", false);
                response.put("error", "参数不能为空");
                return response;
            }
            
            boolean success = vehicleControlService.controlLights(vid, status);
            
            if (success) {
                response.put("success", true);
                response.put("message", "灯光控制指令发送成功");
                response.put("vid", vid);
                response.put("status", status);
            } else {
                response.put("success", false);
                response.put("error", "灯光控制指令发送失败");
            }
            
        } catch (Exception e) {
            log.error("处理灯光控制消息失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "处理灯光控制失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 处理闪烁灯光消息
     */
    @MessageMapping("/vehicle.control.flash")
    @SendToUser("/queue/control.result")
    public Map<String, Object> handleFlashControl(@Payload Map<String, Object> message) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String vid = (String) message.get("vid");
            String pattern = (String) message.get("pattern");
            Integer duration = (Integer) message.get("duration");
            
            if (vid == null || pattern == null || duration == null) {
                response.put("success", false);
                response.put("error", "参数不能为空");
                return response;
            }
            
            boolean success = vehicleControlService.flashLights(vid, pattern, duration);
            
            if (success) {
                response.put("success", true);
                response.put("message", "闪烁灯光指令发送成功");
                response.put("vid", vid);
                response.put("pattern", pattern);
                response.put("duration", duration);
            } else {
                response.put("success", false);
                response.put("error", "闪烁灯光指令发送失败");
            }
            
        } catch (Exception e) {
            log.error("处理闪烁灯光消息失败: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "处理闪烁灯光失败: " + e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 广播控制操作（用于实时显示）
     */
    private void broadcastControlOperation(String vid, ControlCommand command) {
        try {
            Map<String, Object> broadcastMessage = new HashMap<>();
            broadcastMessage.put("type", "control_operation");
            broadcastMessage.put("vid", vid);
            broadcastMessage.put("command", command.getCommand());
            broadcastMessage.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/vehicle.operations", broadcastMessage);
            
        } catch (Exception e) {
            log.error("广播控制操作失败: {}", e.getMessage());
        }
    }
}