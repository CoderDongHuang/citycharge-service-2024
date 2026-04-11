package com.citycharge.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_order")
public class UserOrder {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "vehicle_id")
    private Long vehicleId;
    
    @Column(name = "vehicle_name", length = 100)
    private String vehicleName;
    
    @Column(name = "station_id")
    private Long stationId;
    
    @Column(name = "station_name", length = 100)
    private String stationName;
    
    @Column(name = "battery_info", length = 100)
    private String batteryInfo;
    
    @Column(name = "amount", precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private OrderStatus status = OrderStatus.pending;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "pay_time")
    private LocalDateTime payTime;
    
    @Column(name = "complete_time")
    private LocalDateTime completeTime;
    
    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;
    
    @Column(name = "notes", length = 500)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        pending,
        processing,
        completed,
        cancelled
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
        if (id == null || id.isEmpty()) {
            id = generateOrderId();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    private String generateOrderId() {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = (int) (Math.random() * 1000);
        return "ORD" + timestamp + String.format("%03d", random);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }
    
    public String getVehicleName() { return vehicleName; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    
    public Long getStationId() { return stationId; }
    public void setStationId(Long stationId) { this.stationId = stationId; }
    
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    
    public String getBatteryInfo() { return batteryInfo; }
    public void setBatteryInfo(String batteryInfo) { this.batteryInfo = batteryInfo; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getPayTime() { return payTime; }
    public void setPayTime(LocalDateTime payTime) { this.payTime = payTime; }
    
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
    
    public LocalDateTime getCancelTime() { return cancelTime; }
    public void setCancelTime(LocalDateTime cancelTime) { this.cancelTime = cancelTime; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
