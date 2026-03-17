package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleStatusDTO {
    private String vid;
    private String pid;
    private Integer positionX;
    private Integer positionY;
    private Double voltage;
    private Double temperature;
    private Double batteryCapacity;
    private Boolean isOnline;
    private Boolean headlightStatus;
    private Boolean highbeamStatus;
    private Boolean isAlarming;
    private String alarmType;
    private LocalDateTime lastHeartbeat;
}