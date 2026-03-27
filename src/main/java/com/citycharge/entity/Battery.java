package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battery")
public class Battery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pid", length = 50, unique = true, nullable = false)
    private String pid;
    
    @Column(name = "vid", length = 50)
    private String vid;
    
    @Column(name = "voltage", precision = 5, scale = 2)
    private Double voltage;
    
    @Column(name = "temperature", precision = 5, scale = 2)
    private Double temperature;
    
    @Column(name = "battery_level", precision = 5, scale = 2)
    private Double batteryLevel;
    
    @Column(name = "status", length = 20)
    private String status;
    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        if (lastUpdate == null) {
            lastUpdate = LocalDateTime.now();
        }
        if (status == null) {
            status = "normal";
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdate = LocalDateTime.now();
    }
    
    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public String getVid() { return vid; }
    public void setVid(String vid) { this.vid = vid; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) { this.batteryLevel = batteryLevel; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
}