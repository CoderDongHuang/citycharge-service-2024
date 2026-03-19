package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vid", unique = true, nullable = false)
    private String vid;
    
    @Column(name = "pid")
    private String pid;
    
    @Column(name = "voltage", precision = 5, scale = 2)
    private Double voltage;
    
    @Column(name = "temperature", precision = 5, scale = 2)
    private Double temperature;
    
    @Column(name = "battery_level", precision = 5, scale = 2)
    private Double batteryLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "light_status")
    private LightStatus lightStatus = LightStatus.off;
    
    @Column(name = "position_x")
    private Integer positionX;
    
    @Column(name = "position_y")
    private Integer positionY;
    
    @Column(name = "online_status")
    private Boolean onlineStatus = false;
    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (lastUpdate == null) {
            lastUpdate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        lastUpdate = LocalDateTime.now();
    }
    
    public enum LightStatus {
        off, lowBeam, highBeam
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getVid() { return vid; }
    public void setVid(String vid) { this.vid = vid; }
    
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(Double batteryLevel) { this.batteryLevel = batteryLevel; }
    
    public LightStatus getLightStatus() { return lightStatus; }
    public void setLightStatus(LightStatus lightStatus) { this.lightStatus = lightStatus; }
    
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    
    public Boolean getOnlineStatus() { return onlineStatus; }
    public void setOnlineStatus(Boolean onlineStatus) { this.onlineStatus = onlineStatus; }
    
    public LocalDateTime getLastUpdate() { return lastUpdate; }
    public void setLastUpdate(LocalDateTime lastUpdate) { this.lastUpdate = lastUpdate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}