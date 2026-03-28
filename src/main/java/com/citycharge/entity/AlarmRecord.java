package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alarm_records")
public class AlarmRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vehicle_vid")
    private String vehicleVid;
    
    @Column(name = "battery_pid")
    private String batteryPid;
    
    @Column(name = "alarm_type", nullable = false)
    private String alarmType;
    
    @Column(name = "alarm_message")
    private String alarmMessage;
    
    @Column(name = "alarm_level")
    private String alarmLevel;
    
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
    
    @Column(name = "handled_by")
    private String handledBy;
    
    @Column(name = "handled_at")
    private LocalDateTime handledAt;
    
    @Column(name = "resolve_notes")
    private String resolveNotes;
    
    @PrePersist
    protected void onCreate() {
        alarmTime = LocalDateTime.now();
    }

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getVehicleVid() { return vehicleVid; }
    public void setVehicleVid(String vehicleVid) { this.vehicleVid = vehicleVid; }
    
    public String getBatteryPid() { return batteryPid; }
    public void setBatteryPid(String batteryPid) { this.batteryPid = batteryPid; }
    
    public String getAlarmType() { return alarmType; }
    public void setAlarmType(String alarmType) { this.alarmType = alarmType; }
    
    public String getAlarmMessage() { return alarmMessage; }
    public void setAlarmMessage(String alarmMessage) { this.alarmMessage = alarmMessage; }
    
    public String getAlarmLevel() { return alarmLevel; }
    public void setAlarmLevel(String alarmLevel) { this.alarmLevel = alarmLevel; }
    
    public Double getVoltage() { return voltage; }
    public void setVoltage(Double voltage) { this.voltage = voltage; }
    
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    
    public Double getCapacityPercentage() { return capacityPercentage; }
    public void setCapacityPercentage(Double capacityPercentage) { this.capacityPercentage = capacityPercentage; }
    
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    
    public Boolean getIsResolved() { return isResolved; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
    
    public LocalDateTime getAlarmTime() { return alarmTime; }
    public void setAlarmTime(LocalDateTime alarmTime) { this.alarmTime = alarmTime; }
    
    public LocalDateTime getResolveTime() { return resolveTime; }
    public void setResolveTime(LocalDateTime resolveTime) { this.resolveTime = resolveTime; }
    
    public String getHandledBy() { return handledBy; }
    public void setHandledBy(String handledBy) { this.handledBy = handledBy; }
    
    public LocalDateTime getHandledAt() { return handledAt; }
    public void setHandledAt(LocalDateTime handledAt) { this.handledAt = handledAt; }
    
    public String getResolveNotes() { return resolveNotes; }
    public void setResolveNotes(String resolveNotes) { this.resolveNotes = resolveNotes; }
}