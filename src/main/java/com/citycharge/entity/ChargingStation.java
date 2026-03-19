package com.citycharge.entity;

import javax.persistence.*;

@Entity
@Table(name = "charging_stations")
public class ChargingStation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "station_id", unique = true, nullable = false)
    private String stationId;
    
    @Column(name = "position_x", nullable = false)
    private Integer positionX;
    
    @Column(name = "position_y", nullable = false)
    private Integer positionY;
    
    @Column(name = "available_batteries")
    private Integer availableBatteries = 0;
    
    @Column(name = "total_capacity")
    private Integer totalCapacity = 10;
    
    @Column(name = "is_active")
    private Boolean isActive = true;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    
    public Integer getPositionX() { return positionX; }
    public void setPositionX(Integer positionX) { this.positionX = positionX; }
    
    public Integer getPositionY() { return positionY; }
    public void setPositionY(Integer positionY) { this.positionY = positionY; }
    
    public Integer getAvailableBatteries() { return availableBatteries; }
    public void setAvailableBatteries(Integer availableBatteries) { this.availableBatteries = availableBatteries; }
    
    public Integer getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(Integer totalCapacity) { this.totalCapacity = totalCapacity; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}