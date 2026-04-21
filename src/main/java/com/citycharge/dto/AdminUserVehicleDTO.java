package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AdminUserVehicleDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private String name;
    private String brand;
    private String vin;
    private String plateNumber;
    private String status;
    private Long batteryId;
    private Integer batteryLevel;
    private Double voltage;
    private Double temperature;
    private Boolean online;
    private LocalDateTime lastUpdateTime;
    private LocalDateTime createTime;
    private String source;
}
