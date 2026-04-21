package com.citycharge.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVehicleStatusMessage {
    
    private LocalDateTime timestamp;
    
    @JsonProperty("vehicleId")
    private Long vehicleId;
    
    @JsonProperty("userId")
    private Long userId;
    
    private VehicleInfo vehicle;
    
    private BatteryInfo battery;
    
    @Data
    public static class VehicleInfo {
        private String name;
        private String plateNumber;
        private String status;
        private Integer batteryLevel;
        private Double voltage;
        private Double temperature;
        private Double latitude;
        private Double longitude;
    }
    
    @Data
    public static class BatteryInfo {
        private Long id;
        private String code;
        private String model;
        private Double voltage;
        private Double temperature;
        private Integer currentLevel;
        private String status;
    }
}
