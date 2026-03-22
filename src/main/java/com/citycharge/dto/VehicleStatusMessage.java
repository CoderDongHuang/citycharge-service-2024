package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehicleStatusMessage {
    
    /**
     * 时间戳（ISO 8601格式）
     */
    private String timestamp;
    
    /**
     * 车辆编号
     */
    private String vid;
    
    /**
     * 电池编号
     */
    private String pid;
    
    /**
     * 电池电压（V）
     */
    private Double voltage;
    
    /**
     * 电池温度（°C）
     */
    private Double temperature;
    
    /**
     * 电池剩余电量百分比（%）
     */
    @JsonProperty("battery_level")
    private Double batteryLevel;
    
    /**
     * 灯光状态（off/lowBeam/highBeam）
     */
    @JsonProperty("light_status")
    private String lightStatus;
    
    /**
     * 车辆X坐标
     */
    @JsonProperty("position_x")
    private Integer positionX;
    
    /**
     * 车辆Y坐标
     */
    @JsonProperty("position_y")
    private Integer positionY;
    
    /**
     * 在线状态
     */
    @JsonProperty("online_status")
    private Boolean onlineStatus;
}