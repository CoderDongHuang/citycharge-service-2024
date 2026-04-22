package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AdminUserBatteryDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private String name;
    private String model;
    private String code;
    private Integer capacity;
    private Double voltage;
    private Double temperature;
    private Integer batteryLevel;
    private Integer health;
    private String status;
    private Long currentVehicleId;
    private String currentVehicleName;
    private String currentVehiclePlate;
    private Boolean online;
    private LocalDateTime lastUpdateTime;
    private LocalDate purchaseDate;
    private LocalDateTime createTime;
    private String source;
}
