package com.citycharge.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_records")
@Data
public class AlarmRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vid", nullable = false)
    private String vid;
    
    @Column(name = "pid")
    private String pid;
    
    @Column(name = "alarm_type", nullable = false)
    private String alarmType;
    
    @Column(name = "alarm_message")
    private String alarmMessage;
    
    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "capacity_percentage")
    private Double capacityPercentage;
    
    @Column(name = "position_x")
    private Integer positionX;
    
    @Column(name = "position_y")
    private Integer positionY;
    
    @Column(name = "is_resolved")
    private Boolean isResolved = false;
    
    @Column(name = "alarm_time")
    private LocalDateTime alarmTime;
    
    @Column(name = "resolve_time")
    private LocalDateTime resolveTime;
    
    @PrePersist
    protected void onCreate() {
        alarmTime = LocalDateTime.now();
    }
}