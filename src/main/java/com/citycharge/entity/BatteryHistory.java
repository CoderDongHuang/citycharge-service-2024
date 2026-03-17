package com.citycharge.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battery_history")
@Data
public class BatteryHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pid", nullable = false)
    private String pid;
    
    @Column(name = "vid")
    private String vid;
    
    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "capacity_percentage")
    private Double capacityPercentage;
    
    @Column(name = "record_time")
    private LocalDateTime recordTime;
    
    @PrePersist
    protected void onCreate() {
        recordTime = LocalDateTime.now();
    }
}