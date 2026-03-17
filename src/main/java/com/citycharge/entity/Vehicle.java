package com.citycharge.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vid", unique = true, nullable = false)
    private String vid;
    
    @Column(name = "current_pid")
    private String currentPid;
    
    @Column(name = "position_x")
    private Integer positionX;
    
    @Column(name = "position_y")
    private Integer positionY;
    
    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "battery_capacity")
    private Double batteryCapacity;
    
    @Column(name = "is_online")
    private Boolean isOnline = false;
    
    @Column(name = "headlight_status")
    private Boolean headlightStatus = false;
    
    @Column(name = "highbeam_status")
    private Boolean highbeamStatus = false;
    
    @Column(name = "is_alarming")
    private Boolean isAlarming = false;
    
    @Column(name = "alarm_type")
    private String alarmType;
    
    @Column(name = "last_heartbeat")
    private LocalDateTime lastHeartbeat;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}