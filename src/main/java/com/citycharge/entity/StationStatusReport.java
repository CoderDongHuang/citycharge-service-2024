package com.citycharge.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "station_status_report")
public class StationStatusReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "station_id", nullable = false)
    private Long stationId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private ReportType type;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ReportStatus status = ReportStatus.pending;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ReportType {
        battery_low, device_error, full_battery, other
    }
    
    public enum ReportStatus {
        pending, processing, resolved, closed
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
    
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
