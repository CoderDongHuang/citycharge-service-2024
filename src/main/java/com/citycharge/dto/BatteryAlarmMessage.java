package com.citycharge.dto;

import lombok.Data;

@Data
public class BatteryAlarmMessage {
    
    /**
     * 时间戳
     */
    private String timestamp;
    
    /**
     * 报警类型
     * temperature - 温度异常
     * voltage - 电压异常
     * current - 电流异常
     */
    private String type;
    
    /**
     * 车辆编号
     */
    private String vid;
    
    /**
     * 电池编号
     */
    private String pid;
    
    /**
     * 报警级别
     * high - 高
     * medium - 中
     * low - 低
     */
    private String level;
    
    /**
     * 触发值
     */
    private Double triggerValue;
    
    /**
     * 阈值
     */
    private Double thresholdValue;
    
    /**
     * 位置X坐标
     */
    private Integer positionX;
    
    /**
     * 位置Y坐标
     */
    private Integer positionY;
    
    /**
     * 报警消息
     */
    private String message;
    
    /**
     * 检查是否为温度报警
     */
    public boolean isTemperatureAlarm() {
        return "temperature".equals(type);
    }
    
    /**
     * 检查是否为电压报警
     */
    public boolean isVoltageAlarm() {
        return "voltage".equals(type);
    }
    
    /**
     * 检查是否为高优先级报警
     */
    public boolean isHighLevel() {
        return "high".equals(level);
    }
    
    /**
     * 检查是否为中优先级报警
     */
    public boolean isMediumLevel() {
        return "medium".equals(level);
    }
    
    /**
     * 检查是否为低优先级报警
     */
    public boolean isLowLevel() {
        return "low".equals(level);
    }
}