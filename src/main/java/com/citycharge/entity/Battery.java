package com.citycharge.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batteries")
@Data
public class Battery {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pid", unique = true, nullable = false)
    private String pid;
    
    @Column(name = "current_vid")
    private String currentVid;
    
    @Column(name = "voltage")
    private Double voltage;
    
    @Column(name = "temperature")
    private Double temperature;
    
    @Column(name = "capacity_percentage")
    private Double capacityPercentage;
    
    @Column(name = "is_in_use")
    private Boolean isInUse = false;
    
    @Column(name = "is_available")
    private Boolean isAvailable = true;
    
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}