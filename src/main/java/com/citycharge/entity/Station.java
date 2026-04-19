package com.citycharge.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "station")
public class Station {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "station_id", unique = true, nullable = false, length = 50)
    private String stationId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private Type type = Type.battery;
    
    @Column(name = "position_x")
    private Integer positionX;
    
    @Column(name = "position_y")
    private Integer positionY;
    
    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;
    
    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "battery_capacity")
    private Integer batteryCapacity = 10;
    
    @Column(name = "available_batteries")
    private Integer availableBatteries = 0;
    
    @Column(name = "available_slots")
    private Integer availableSlots = 0;
    
    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating = new BigDecimal("5.0");
    
    @Column(name = "total_swaps")
    private Integer totalSwaps = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status = Status.active;
    
    @Column(name = "service_time", length = 50)
    private String serviceTime;
    
    @Column(name = "operating_hours", length = 100)
    private String operatingHours;
    
    @Column(name = "contact_phone", length = 20)
    private String contactPhone;
    
    @Column(name = "manager", length = 50)
    private String manager;
    
    @Column(name = "facilities", columnDefinition = "JSON")
    private String facilities;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum Status {
        active, maintenance, closed, offline, online
    }
    
    public enum Type {
        battery, service, all
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = Status.active;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(Integer batteryCapacity) { this.batteryCapacity = batteryCapacity; }
    
    public Integer getAvailableBatteries() { return availableBatteries; }
    public void setAvailableBatteries(Integer availableBatteries) { this.availableBatteries = availableBatteries; }
    
    public Integer getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(Integer availableSlots) { this.availableSlots = availableSlots; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public Integer getTotalSwaps() { return totalSwaps; }
    public void setTotalSwaps(Integer totalSwaps) { this.totalSwaps = totalSwaps; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public String getServiceTime() { return serviceTime; }
    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }
    
    public String getOperatingHours() { return operatingHours; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getManager() { return manager; }
    public void setManager(String manager) { this.manager = manager; }
    
    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
