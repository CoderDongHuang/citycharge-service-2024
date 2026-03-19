package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "battery_history")
public class BatteryHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pid", nullable = false)
    private String pid;
    
    @Column(name = "vid")
    private String vid;
    
    @Column(name = "voltage", precision = 5, scale = 2)
    private Double voltage;
    
    @Column(name = "temperature", precision = 5, scale = 2)
    private Double temperature;
    
    @Column(name = "capacity", precision = 5, scale = 2)
    private Double capacity;
    
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
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
    
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}