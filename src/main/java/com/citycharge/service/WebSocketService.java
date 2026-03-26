package com.citycharge.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    public void sendVehicleStatusUpdate(String vid, Object status) {
        messagingTemplate.convertAndSend("/topic/vehicle/" + vid + "/status", status);
    }
    
    public void sendControlCommand(String vid, Object command) {
        messagingTemplate.convertAndSend("/topic/vehicle/" + vid + "/control", command);
    }
    
    public void broadcastSystemMessage(Object message) {
        messagingTemplate.convertAndSend("/topic/system", message);
    }
    
    public void sendAlarmNotification(Object alarm) {
        messagingTemplate.convertAndSend("/topic/alarms", alarm);
    }
    
    public void sendEmergencyNotification(Object emergencyMsg) {
        messagingTemplate.convertAndSend("/topic/emergency", emergencyMsg);
    }
}