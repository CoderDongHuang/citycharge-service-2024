package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battery")
public class Battery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pid", unique = true, nullable = false)
    private String pid;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BatteryStatus status = BatteryStatus.available;
    
    @Column(name = "current_vehicle")
    private String currentVehicle;
    
    @Column(name = "voltage", precision = 5, scale = 2)
    private Double voltage;
    
    @Column(name = "temperature", precision = 5, scale = 2)
    private Double temperature;
    
    @Column(name = "remaining_capacity", precision = 5, scale = 2)
    private Double remainingCapacity;
    
    @Column(name = "health", precision = 5, scale = 2)
    private Double health;
    
    @Column(name = "v_min", precision = 5, scale = 2)
    private Double vMin = 3.0;
    
    @Column(name = "v_max", precision = 5, scale = 2)
    private Double vMax = 4.2;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum BatteryStatus {
        inUse, available, maintenance
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }
    
    public BatteryStatus getStatus() { return status; }
    public void setStatus(BatteryStatus status) { this.status = status; }
    
    public String getCurrentVehicle() { return currentVehicle; }
    public void setCurrentVehicle(String currentVehicle) { this.currentVehicle = currentVehicle; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getRemainingCapacity() { return remainingCapacity; }
    public void setRemainingCapacity(Double remainingCapacity) { this.remainingCapacity = remainingCapacity; }
    
    public Double getHealth() { return health; }
    public void setHealth(Double health) { this.health = health; }
    
    public Double getVMin() { return vMin; }
    public void setVMin(Double vMin) { this.vMin = vMin; }
    
    public Double getVMax() { return vMax; }
    public void setVMax(Double vMax) { this.vMax = vMax; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}