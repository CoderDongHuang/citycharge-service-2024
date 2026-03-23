package com.citycharge.dto;

import lombok.Data;

@Data
public class WebSocketControlMessage {
    
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 车辆编号
     */
    private String vid;
    
    /**
     * 控制指令
     */
    private ControlCommand command;
    
    /**
     * 用户ID（用于权限验证）
     */
    private String userId;
    
    /**
     * 消息ID（用于追踪）
     */
    private String messageId;
}