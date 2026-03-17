package com.citycharge.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "charging_stations")
@Data
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
}