package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_battery")
public class UserBattery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "model", nullable = false, length = 100)
    private String model;
    
    @Column(name = "code", nullable = false, length = 50, unique = true)
    private String code;
    
    @Column(name = "capacity")
    private Integer capacity;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BatteryStatus status = BatteryStatus.offline;
    
    @Column(name = "current_level")
    private Integer currentLevel;
    
    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "cycle_count")
    private Integer cycleCount;
    
    @Column(name = "current_vehicle_id")
    private Long currentVehicleId;
    
    @Column(name = "last_charge_time")
    private LocalDateTime lastChargeTime;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum BatteryStatus {
        online, offline, charging
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public BatteryStatus getStatus() { return status; }
    public void setStatus(BatteryStatus status) { this.status = status; }
    
    public Integer getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Integer currentLevel) { this.currentLevel = currentLevel; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Integer getCycleCount() { return cycleCount; }
    public void setCycleCount(Integer cycleCount) { this.cycleCount = cycleCount; }
    
    public Long getCurrentVehicleId() { return currentVehicleId; }
    public void setCurrentVehicleId(Long currentVehicleId) { this.currentVehicleId = currentVehicleId; }
    
    public LocalDateTime getLastChargeTime() { return lastChargeTime; }
    public void setLastChargeTime(LocalDateTime lastChargeTime) { this.lastChargeTime = lastChargeTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
