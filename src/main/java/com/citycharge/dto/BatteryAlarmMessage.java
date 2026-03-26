package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
     * lowBattery - 电量过低
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
     * critical - 严重
     * high - 高
     * medium - 中
     * low - 低
     */
    private String level;
    
    /**
     * 触发值
     */
    @JsonProperty("trigger_value")
    private Double triggerValue;
    
    /**
     * 阈值
     */
    @JsonProperty("threshold_value")
    private Double thresholdValue;
    
    /**
     * 位置X坐标
     */
    @JsonProperty("position_x")
    private Integer positionX;
    
    /**
     * 位置Y坐标
     */
    @JsonProperty("position_y")
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
     * 检查是否为电量过低报警
     */
    public boolean isLowBatteryAlarm() {
        return "lowBattery".equals(type);
    }
    
    /**
     * 检查是否为严重级别报警
     */
    public boolean isCriticalLevel() {
        return "critical".equals(level);
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