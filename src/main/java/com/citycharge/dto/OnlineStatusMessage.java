package com.citycharge.dto;

import lombok.Data;

/**
 * 车辆在线状态消息DTO
 * 用于解析MQTT在线状态消息
 */
@Data
public class OnlineStatusMessage {
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 车辆编号
     */
    private String vid;
    
    /**
     * 在线状态
     * online - 上线
     * offline - 下线
     */
    private String status;
    
    /**
     * IP地址（上线时提供）
     */
    private String ip;
    
    /**
     * 下线原因（下线时提供）
     */
    private String reason;
    
    /**
     * 检查是否为上线状态
     */
    public boolean isOnline() {
        return "online".equals(status);
    }
    
    /**
     * 检查是否为下线状态
     */
    public boolean isOffline() {
        return "offline".equals(status);
    }
}