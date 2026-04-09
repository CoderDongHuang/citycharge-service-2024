package com.citycharge.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVehicleStatusDTO {
    private Long id;
    private String status;
    private LocalDateTime lastOnlineTime;
    private Integer batteryLevel;
    private Location location;
    
    @Data
    public static class Location {
        private Double latitude;
        private Double longitude;
    }
}
