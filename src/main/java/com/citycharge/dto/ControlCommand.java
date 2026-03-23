package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ControlCommand {
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 控制指令类型
     * 可选值: flashLights, setLightStatus, setOnlineStatus, etc.
     */
    private String command;
    
    /**
     * 指令参数
     */
    private Map<String, Object> params;
    
    /**
     * 创建时间戳
     */
    public ControlCommand() {
        this.timestamp = LocalDateTime.now().toString();
    }
    
    /**
     * 带参数的构造函数
     */
    public ControlCommand(String command, Map<String, Object> params) {
        this();
        this.command = command;
        this.params = params;
    }
}